package com.budgetApp.data.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "expenses")
public class Expense extends Transaction {

    private String userId;
    private Category category;
    private String description;
}
