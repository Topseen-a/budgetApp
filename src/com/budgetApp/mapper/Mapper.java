package com.budgetApp.mapper;

import com.budgetApp.data.models.*;
import com.budgetApp.dtos.requests.AddExpenseRequest;
import com.budgetApp.dtos.requests.AddIncomeRequest;
import com.budgetApp.dtos.requests.CreateBudgetRequest;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.AddExpenseResponse;
import com.budgetApp.dtos.responses.AddIncomeResponse;
import com.budgetApp.dtos.responses.CreateBudgetResponse;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.InvalidRequestException;

public class Mapper {

    public static User toUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setPassword(createUserRequest.getPassword());
        return user;
    }

    public static CreateUserResponse toUserResponse(User user) {
        CreateUserResponse response = new CreateUserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }

    public static Income toIncome(AddIncomeRequest addIncomeRequest) {
        Income income = new Income();
        income.setUserId(addIncomeRequest.getUserId());
        income.setAmount(addIncomeRequest.getAmount());
        income.setSource(addIncomeRequest.getSource());
        income.setDate(addIncomeRequest.getDate());
        return income;
    }

    public static AddIncomeResponse toIncomeResponse(Income income) {
        AddIncomeResponse response = new AddIncomeResponse();
        response.setId(income.getId());
        response.setAmount(income.getAmount());
        response.setSource(income.getSource());
        response.setDate(income.getDate());
        return response;
    }

    public static Expense toExpense(AddExpenseRequest addExpenseRequest) {
        Expense expense = new Expense();
        expense.setUserId(addExpenseRequest.getUserId());
        expense.setAmount(addExpenseRequest.getAmount());
        try {
            expense.setCategory(Category.valueOf(addExpenseRequest.getCategory().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid category value");
        }
        expense.setDescription(addExpenseRequest.getDescription());
        expense.setDate(addExpenseRequest.getDate());
        return expense;
    }

    public static AddExpenseResponse toExpenseResponse(Expense expense) {
        AddExpenseResponse response = new AddExpenseResponse();
        response.setId(expense.getId());
        response.setAmount(expense.getAmount());
        response.setCategory(expense.getCategory().toString());
        response.setDescription(expense.getDescription());
        response.setDate(expense.getDate());
        return response;
    }

    public static Budget toBudget(CreateBudgetRequest createBudgetRequest) {
        Budget budget = new Budget();
        budget.setUserId(createBudgetRequest.getUserId());
        try {
            budget.setCategory(Category.valueOf(createBudgetRequest.getCategory().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Invalid category value");
        }
        budget.setLimitAmount(createBudgetRequest.getLimitAmount());
        budget.setStartDate(createBudgetRequest.getStartDate());
        budget.setEndDate(createBudgetRequest.getEndDate());
        return budget;
    }

    public static CreateBudgetResponse toCreatBudgetResponse(Budget budget) {
        CreateBudgetResponse response = new CreateBudgetResponse();
        response.setId(budget.getId());
        response.setCategory(budget.getCategory().toString());
        response.setLimitAmount(budget.getLimitAmount());
        response.setStartDate(budget.getStartDate());
        response.setEndDate(budget.getEndDate());
        response.setCurrentSpent(budget.getCurrentSpent());
        return response;
    }
}
