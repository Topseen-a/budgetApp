package com.budgetApp.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private List<String> expenseIds = new ArrayList<>();
    private List<String> incomeIds = new ArrayList<>();
    private List<String> budgetIds = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
}
