package com.budgetApp.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
public abstract class Transaction {

    @Id
    private String id;
    private Double amount;
    private LocalDate date;
}
