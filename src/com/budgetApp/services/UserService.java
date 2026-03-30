package com.budgetApp.services;

import com.budgetApp.data.models.User;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.InvalidEmailException;
import com.budgetApp.exceptions.InvalidNameException;
import com.budgetApp.exceptions.InvalidPasswordException;
import com.budgetApp.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        validateUserRequest(request);

        User user = Mapper.toUser(request);
        User savedUser = userRepository.save(user);
        return Mapper.toUserResponse(savedUser);
    }



    private void validateUserRequest(CreateUserRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidNameException("Username cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException("Email format is invalid");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty() || request.getPassword().length() < 6) {
            throw new InvalidPasswordException("Password cannot be empty");
        }
    }
}
