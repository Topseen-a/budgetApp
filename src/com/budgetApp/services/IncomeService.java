package com.budgetApp.services;

import com.budgetApp.data.models.Income;
import com.budgetApp.data.models.User;
import com.budgetApp.data.repositories.IncomeRepository;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.AddIncomeRequest;
import com.budgetApp.dtos.responses.AddIncomeResponse;
import com.budgetApp.exceptions.IncomeNotFoundException;
import com.budgetApp.exceptions.InvalidRequestException;
import com.budgetApp.exceptions.UserNotFoundException;
import com.budgetApp.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    public AddIncomeResponse addIncome(AddIncomeRequest request) {
        validateIncomeRequest(request);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Income income = Mapper.toIncome(request);
        Income savedIncome = incomeRepository.save(income);

        user.getIncomeIds().add(savedIncome.getId());
        userRepository.save(user);

        return Mapper.toIncomeResponse(savedIncome);
    }

    public List<AddIncomeResponse> getAllIncomesByUser(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return incomeRepository.findByUserId(userId)
                .stream()
                .map(Mapper::toIncomeResponse)
                .toList();
    }

    public AddIncomeResponse getIncomeById(String id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));

        return Mapper.toIncomeResponse(income);
    }

    public AddIncomeResponse updateIncome(String id, AddIncomeRequest request) {
        validateIncomeRequest(request);

        Income existingIncome = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));

        existingIncome.setAmount(request.getAmount());
        existingIncome.setSource(request.getSource());
        existingIncome.setDate(request.getDate());

        Income savedIncome = incomeRepository.save(existingIncome);

        return Mapper.toIncomeResponse(savedIncome);
    }

    public void deleteIncome(String id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income not found"));

        User user = userRepository.findById(income.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.getIncomeIds().remove(id);
        userRepository.save(user);

        incomeRepository.deleteById(id);
    }

    private void validateIncomeRequest(AddIncomeRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new InvalidRequestException("User ID is required");
        }
        if (request.getAmount() <= 0) {
            throw new InvalidRequestException("Amount must be greater than 0");
        }
        if (request.getSource() == null || request.getSource().trim().isEmpty()) {
            throw new InvalidRequestException("Source cannot be empty");
        }
        if (request.getDate() == null) {
            throw new InvalidRequestException("Date cannot be empty");
        }
    }
}
