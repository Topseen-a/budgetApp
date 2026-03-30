package com.budgetApp.dtos.responses;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBudgetResponse {

    private String id;
    private String category;
    private double limitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private double currentSpent;
}
