package com.budgetApp.data.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "income")
public class Income extends Transaction {

    private String userId;
    private String source;
}
