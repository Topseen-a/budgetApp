package com.budgetApp.services;

import com.budgetApp.data.repositories.ExpenseRepository;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.AddExpenseRequest;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.AddExpenseResponse;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private String userId;

    @BeforeEach
    public void setUp() {
        expenseRepository.deleteAll();
        userRepository.deleteAll();

        CreateUserRequest user = new CreateUserRequest();
        user.setName("Oluwaseun");
        user.setEmail("oluwaseun@gmail.com");
        user.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(user);
        userId = response.getId();
    }

    @Test
    public void testThatExpenseCanBeAdded() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(5_000);
        request.setCategory("FOOD");
        request.setDescription("Lunch");
        request.setDate(LocalDate.now());

        AddExpenseResponse response = expenseService.addExpense(request);

        assertEquals(5_000, response.getAmount());
        assertEquals("FOOD", response.getCategory());
        assertEquals(1, expenseRepository.count());
    }

    @Test
    public void testThatAddingExpenseWithInvalidUserThrowsException() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId("123-456");
        request.setAmount(5_000);
        request.setCategory("FOOD");
        request.setDescription("Lunch");
        request.setDate(LocalDate.now());

        assertThrows(UserNotFoundException.class, () -> expenseService.addExpense(request));
    }

    @Test
    public void testThatInvalidCategoryThrowsException() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(2000);
        request.setCategory("PARTY");
        request.setDescription("Lunch");
        request.setDate(LocalDate.now());

        assertThrows(InvalidRequestException.class, () -> expenseService.addExpense(request));
    }

    @Test
    public void testThatInvalidAmountThrowsException() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(0);
        request.setCategory("FOOD");
        request.setDescription("Lunch");
        request.setDate(LocalDate.now());

        assertThrows(InvalidRequestException.class, () -> expenseService.addExpense(request));
    }

    @Test
    public void testThatExpenseCanBeGottenById() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(3_000);
        request.setCategory("TRANSPORT");
        request.setDescription("Taxi");
        request.setDate(LocalDate.now());

        AddExpenseResponse response = expenseService.addExpense(request);

        AddExpenseResponse found = expenseService.getExpenseById(response.getId());

        assertEquals("TRANSPORT", found.getCategory());
    }

    @Test
    public void testThatGettingInvalidExpenseThrowsException() {
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpenseById("123-456"));
    }


    @Test
    public void testThatUserCanHaveMultipleExpenses() {
        AddExpenseRequest requestOne = new AddExpenseRequest();
        requestOne.setUserId(userId);
        requestOne.setAmount(1_000);
        requestOne.setCategory("FOOD");
        requestOne.setDescription("Meal");
        requestOne.setDate(LocalDate.now());

        expenseService.addExpense(requestOne);

        AddExpenseRequest requestTwo = new AddExpenseRequest();
        requestTwo.setUserId(userId);
        requestTwo.setAmount(500);
        requestTwo.setCategory("TRANSPORT");
        requestTwo.setDescription("Bus");
        requestTwo.setDate(LocalDate.now());

        expenseService.addExpense(requestTwo);

        assertEquals(2, expenseService.getAllExpensesByUser(userId).size());
    }

    @Test
    public void testThatExpenseCanBeUpdated() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(1_000);
        request.setCategory("FOOD");
        request.setDescription("Dinner");
        request.setDate(LocalDate.now());

        AddExpenseResponse response = expenseService.addExpense(request);

        AddExpenseRequest update = new AddExpenseRequest();
        update.setUserId(userId);
        update.setAmount(5_000);
        update.setCategory("RENT");
        update.setDescription("For the year 2026");
        update.setDate(LocalDate.now());

        AddExpenseResponse updated = expenseService.updateExpense(response.getId(), update);

        assertEquals(5_000, updated.getAmount());
        assertEquals("RENT", updated.getCategory());
    }

    @Test
    public void testThatUpdatingInvalidExpenseThrowsException() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(1_000);
        request.setCategory("FOOD");
        request.setDescription("Lunch");
        request.setDate(LocalDate.now());

        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateExpense("123-456", request));
    }

    @Test
    public void testThatExpenseCanBeDeleted() {
        AddExpenseRequest request = new AddExpenseRequest();
        request.setUserId(userId);
        request.setAmount(2_000);
        request.setCategory("FOOD");
        request.setDescription("Lunch");
        request.setDate(LocalDate.now());

        AddExpenseResponse response = expenseService.addExpense(request);

        expenseService.deleteExpense(response.getId());

        assertEquals(0, expenseRepository.count());
    }

    @Test
    public void testThatDeletingInvalidExpenseThrowsException() {
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteExpense("123-456"));
    }
}