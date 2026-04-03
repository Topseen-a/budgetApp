package com.budgetApp.controllers;

import com.budgetApp.dtos.requests.AddIncomeRequest;
import com.budgetApp.dtos.responses.AddIncomeResponse;
import com.budgetApp.exceptions.*;
import com.budgetApp.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> addIncome(@RequestBody AddIncomeRequest request) {
        try {
            AddIncomeResponse response = incomeService.addIncome(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAllIncomesByUser(@PathVariable String userId) {
        try {
            List<AddIncomeResponse> incomes = incomeService.getAllIncomesByUser(userId);
            return new ResponseEntity<>(incomes, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to fetch incomes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable String id) {
        try {
            AddIncomeResponse response = incomeService.getIncomeById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IncomeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable String id, @RequestBody AddIncomeRequest request) {
        try {
            AddIncomeResponse response = incomeService.updateIncome(id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IncomeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable String id) {
        try {
            incomeService.deleteIncome(id);
            return new ResponseEntity<>("Income deleted successfully", HttpStatus.OK);
        } catch (IncomeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}