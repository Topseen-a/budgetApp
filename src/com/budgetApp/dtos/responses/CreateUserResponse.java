package com.budgetApp.dtos.responses;

import lombok.Data;

@Data
public class CreateUserResponse {

    private String id;
    private String name;
    private String email;
}
