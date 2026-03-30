package com.budgetApp.dtos.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddExpenseRequest {

    private String userId;
    private double amount;
    private String source;
    private LocalDate date;
}
