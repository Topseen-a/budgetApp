package com.budgetApp.dtos.responses;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddIncomeResponse {

    private String id;
    private double amount;
    private String source;
    private LocalDate date;

}
