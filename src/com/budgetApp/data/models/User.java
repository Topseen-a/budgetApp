package com.budgetApp.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private List<String> expenseIds;
    private List<String> incomeIds;
    private List<String> budgetIds;
    private LocalDateTime createdAt = LocalDateTime.now();
}
