package com.budgetApp.services;

import com.budgetApp.data.models.User;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.*;
import com.budgetApp.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        validateUserRequest(request);

        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = Mapper.toUser(request);
        User savedUser = userRepository.save(user);
        return Mapper.toUserResponse(savedUser);
    }

    public List<CreateUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(Mapper::toUserResponse)
                .toList();
    }

    public CreateUserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return Mapper.toUserResponse(user);
    }

    public CreateUserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        return Mapper.toUserResponse(user);
    }

    public CreateUserResponse updateUser(String id, CreateUserRequest request) {
        validateUserRequest(request);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        String newEmail = request.getEmail().trim();

        if (!existingUser.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        existingUser.setName(request.getName().trim());
        existingUser.setEmail(newEmail);
        existingUser.setPassword(request.getPassword().trim());

        User savedUser = userRepository.save(existingUser);
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
