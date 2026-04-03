package com.budgetApp.services;

import com.budgetApp.data.models.Budget;
import com.budgetApp.data.models.Expense;
import com.budgetApp.data.models.User;
import com.budgetApp.data.repositories.BudgetRepository;
import com.budgetApp.data.repositories.ExpenseRepository;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.AddExpenseRequest;
import com.budgetApp.dtos.responses.AddExpenseResponse;
import com.budgetApp.exceptions.*;
import com.budgetApp.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public AddExpenseResponse addExpense(AddExpenseRequest request) {
        validateExpenseRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Expense expense = Mapper.toExpense(request);
        Expense savedExpense = expenseRepository.save(expense);

        user.getExpenseIds().add(savedExpense.getId());
        userRepository.save(user);

        updateBudgetsForExpense(savedExpense);

        return Mapper.toExpenseResponse(savedExpense);
    }

    public List<AddExpenseResponse> getAllExpensesByUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return expenseRepository.findByUserId(userId)
                .stream()
                .map(Mapper::toExpenseResponse)
                .toList();
    }

    public AddExpenseResponse getExpenseById(String id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        return Mapper.toExpenseResponse(expense);
    }

    public AddExpenseResponse updateExpense(String id, AddExpenseRequest request) {
        validateExpenseRequest(request);

        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        Expense updatedExpense = Mapper.toExpense(request);

        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setCategory(updatedExpense.getCategory());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setDate(updatedExpense.getDate());

        Expense savedExpense = expenseRepository.save(existingExpense);

        updateBudgetsForExpense(savedExpense);

        return Mapper.toExpenseResponse(savedExpense);
    }

    public void deleteExpense(String id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        User user = userRepository.findById(expense.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.getExpenseIds().remove(id);
        userRepository.save(user);

        expenseRepository.deleteById(id);

        updateBudgetsAfterExpenseDeletion(expense);
    }

    private void validateExpenseRequest(AddExpenseRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new InvalidRequestException("User ID cannot be empty");
        }
        if (request.getAmount() <= 0) {
            throw new InvalidRequestException("Amount must be greater than zero");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new InvalidRequestException("Category cannot be empty");
        }
        if (request.getDate() == null) {
            throw new InvalidRequestException("Date cannot be empty");
        }
    }

    private void updateBudgetsForExpense(Expense expense) {
        List<Budget> budgets = budgetRepository.findByUserId(expense.getUserId());

        for (Budget budget : budgets) {
            boolean isCategoryMatch = expense.getCategory().toString().equalsIgnoreCase(budget.getCategory().toString());
            boolean isDateMatch = !expense.getDate().isBefore(budget.getStartDate()) &&
                    !expense.getDate().isAfter(budget.getEndDate());

            if (isCategoryMatch && isDateMatch) {
                double totalSpent = expenseRepository.findByUserId(expense.getUserId())
                        .stream()
                        .filter(e -> e.getCategory().toString().equalsIgnoreCase(budget.getCategory().toString()))
                        .filter(e -> !e.getDate().isBefore(budget.getStartDate()) && !e.getDate().isAfter(budget.getEndDate()))
                        .mapToDouble(Expense::getAmount)
                        .sum();

                budget.setCurrentSpent(totalSpent);
                budgetRepository.save(budget);
            }
        }
    }

    private void updateBudgetsAfterExpenseDeletion(Expense expense) {
        updateBudgetsForExpense(expense);
    }
}