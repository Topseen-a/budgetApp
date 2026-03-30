package com.budgetApp.dtos.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateBudgetRequest {

    private String userId;
    private String category;
    private double limitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
