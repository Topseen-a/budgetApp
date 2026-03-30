package com.budgetApp.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "budget")
public class Budget {

    @Id
    private String id;
    private String userId;
    private Category category;
    private Double limitAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double currentSpent = 0.0;
}
