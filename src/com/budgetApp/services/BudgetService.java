package com.budgetApp.services;

import com.budgetApp.data.models.Budget;
import com.budgetApp.data.models.Expense;
import com.budgetApp.data.models.User;
import com.budgetApp.data.repositories.BudgetRepository;
import com.budgetApp.data.repositories.ExpenseRepository;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.CreateBudgetRequest;
import com.budgetApp.dtos.responses.CreateBudgetResponse;
import com.budgetApp.exceptions.*;
import com.budgetApp.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public CreateBudgetResponse createBudget(CreateBudgetRequest request) {
        validateBudgetRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean exists = budgetRepository.findByUserId(request.getUserId())
                .stream()
                .anyMatch(b -> b.getCategory().toString()
                        .equalsIgnoreCase(request.getCategory()));

        if (exists) {
            throw new BudgetAlreadyExistsException("Budget already exists for this category");
        }

        Budget budget = Mapper.toBudget(request);

        double totalSpent = expenseRepository.findByUserId(request.getUserId())
                .stream()
                .filter(expense -> expense.getCategory().toString().equalsIgnoreCase(request.getCategory()))
                .filter(expense -> !expense.getDate().isBefore(request.getStartDate()) && !expense.getDate().isAfter(request.getEndDate()))
                .mapToDouble(Expense::getAmount)
                .sum();

        budget.setCurrentSpent(totalSpent);

        Budget saved = budgetRepository.save(budget);

        user.getBudgetIds().add(saved.getId());
        userRepository.save(user);

        return Mapper.toBudgetResponse(saved);
    }

    public List<CreateBudgetResponse> getBudgetsByUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return budgetRepository.findByUserId(userId)
                .stream()
                .map(Mapper::toBudgetResponse)
                .toList();
    }

    public CreateBudgetResponse updateBudget(String id, CreateBudgetRequest request) {
        validateBudgetRequest(request);

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        Budget updated = Mapper.toBudget(request);

        budget.setCategory(updated.getCategory());
        budget.setLimitAmount(updated.getLimitAmount());
        budget.setStartDate(updated.getStartDate());
        budget.setEndDate(updated.getEndDate());

        Budget saved = budgetRepository.save(budget);
        return Mapper.toBudgetResponse(saved);
    }

    public void deleteBudget(String id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        User user = userRepository.findById(budget.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.getBudgetIds().remove(id);
        userRepository.save(user);

        budgetRepository.deleteById(id);
    }

    public boolean isBudgetExceeded(String budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found"));

        return budget.getCurrentSpent() > budget.getLimitAmount();
    }

    private void validateBudgetRequest(CreateBudgetRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new InvalidRequestException("User ID cannot be empty");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new InvalidRequestException("Category cannot be empty");
        }
        if (request.getLimitAmount() <= 0) {
            throw new InvalidRequestException("Limit must be greater than zero");
        }
        if (request.getStartDate() == null) {
            throw new InvalidRequestException("Start date cannot be null");
        }
        if (request.getEndDate() == null) {
            throw new InvalidRequestException("End date cannot be null");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidRequestException("End date cannot be before start date");
        }
    }
}