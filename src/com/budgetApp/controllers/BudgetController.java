package com.budgetApp.controllers;

import com.budgetApp.dtos.requests.CreateBudgetRequest;
import com.budgetApp.dtos.responses.CreateBudgetResponse;
import com.budgetApp.exceptions.*;
import com.budgetApp.services.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody CreateBudgetRequest request) {
        try {
            CreateBudgetResponse response = budgetService.createBudget(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BudgetAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new  ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBudgets(@PathVariable String userId) {
        try {
            List<CreateBudgetResponse> budgets = budgetService.getBudgetsByUser(userId);
            return new ResponseEntity<>(budgets, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable String id, @RequestBody CreateBudgetRequest request) {
        try {
            CreateBudgetResponse response = budgetService.updateBudget(id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BudgetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable String id) {
        try {
            budgetService.deleteBudget(id);
            return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
        } catch (BudgetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exceeded/{id}")
    public ResponseEntity<?> isBudgetExceeded(@PathVariable String id) {
        try {
            boolean result = budgetService.isBudgetExceeded(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (BudgetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}