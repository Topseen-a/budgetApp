package com.budgetApp.services;

import com.budgetApp.data.repositories.BudgetRepository;
import com.budgetApp.data.repositories.ExpenseRepository;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.AddExpenseRequest;
import com.budgetApp.dtos.requests.CreateBudgetRequest;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.CreateBudgetResponse;
import com.budgetApp.exceptions.BudgetAlreadyExistsException;
import com.budgetApp.exceptions.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private String userId;

    @BeforeEach
    public void setUp() {
        budgetRepository.deleteAll();
        expenseRepository.deleteAll();
        userRepository.deleteAll();

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setName("Oluwaseun");
        userRequest.setEmail("oluwaseun@gmail.com");
        userRequest.setPassword("oluwaseun1234");

        userId = userService.createUser(userRequest).getId();
    }

    @Test
    public void testThatBudgetCanBeCreated() {
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setUserId(userId);
        request.setCategory("FOOD");
        request.setLimitAmount(10_000);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(5));

        budgetService.createBudget(request);

        assertEquals(1, budgetRepository.count());
    }

    @Test
    public void testThatDuplicateCategoryThrowsException() {
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setUserId(userId);
        request.setCategory("FOOD");
        request.setLimitAmount(10_000);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(5));

        budgetService.createBudget(request);

        CreateBudgetRequest duplicate = new CreateBudgetRequest();
        duplicate.setUserId(userId);
        duplicate.setCategory("FOOD");
        duplicate.setLimitAmount(15_000);
        duplicate.setStartDate(LocalDate.now());
        duplicate.setEndDate(LocalDate.now().plusDays(5));

        assertThrows(BudgetAlreadyExistsException.class, () -> budgetService.createBudget(duplicate));
    }

    @Test
    public void testThatInvalidDateThrowsException() {
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setUserId(userId);
        request.setCategory("FOOD");
        request.setLimitAmount(10_000);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().minusDays(1));

        assertThrows(InvalidRequestException.class, () -> budgetService.createBudget(request));
    }

    @Test
    public void testThatBudgetWithEmptyFieldsThrowsException() {
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setUserId(userId);

        assertThrows(InvalidRequestException.class, () -> budgetService.createBudget(request));
    }

    @Test
    public void testThatBudgetCanBeDeleted() {
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setUserId(userId);
        request.setCategory("FOOD");
        request.setLimitAmount(10_000);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(5));

        CreateBudgetResponse response = budgetService.createBudget(request);
        budgetService.deleteBudget(response.getId());

        assertEquals(0, budgetRepository.count());
    }

    @Test
    public void testThatBudgetCanBeUpdated() {
        CreateBudgetRequest request = new CreateBudgetRequest();
        request.setUserId(userId);
        request.setCategory("FOOD");
        request.setLimitAmount(10_000);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(5));

        CreateBudgetResponse response = budgetService.createBudget(request);

        CreateBudgetRequest updateRequest = new CreateBudgetRequest();
        updateRequest.setUserId(userId);
        updateRequest.setCategory("FOOD");
        updateRequest.setLimitAmount(20_000);
        updateRequest.setStartDate(LocalDate.now());
        updateRequest.setEndDate(LocalDate.now().plusDays(10));

        CreateBudgetResponse updated = budgetService.updateBudget(response.getId(), updateRequest);

        assertEquals(20_000, updated.getLimitAmount());
        assertEquals(LocalDate.now().plusDays(10), updated.getEndDate());
    }

    @Test
    public void testThatBudgetsCanBeGottenByUser() {
        CreateBudgetRequest requestOne = new CreateBudgetRequest();
        requestOne.setUserId(userId);
        requestOne.setCategory("FOOD");
        requestOne.setLimitAmount(10_000);
        requestOne.setStartDate(LocalDate.now());
        requestOne.setEndDate(LocalDate.now().plusDays(5));
        budgetService.createBudget(requestOne);

        CreateBudgetRequest requestTwo = new CreateBudgetRequest();
        requestTwo.setUserId(userId);
        requestTwo.setCategory("TRANSPORT");
        requestTwo.setLimitAmount(5_000);
        requestTwo.setStartDate(LocalDate.now());
        requestTwo.setEndDate(LocalDate.now().plusDays(5));
        budgetService.createBudget(requestTwo);

        assertEquals(2, budgetService.getBudgetsByUser(userId).size());
    }

    @Test
    public void testThatBudgetCurrentSpentUpdatesWithExpenses() {
        CreateBudgetRequest budgetRequest = new CreateBudgetRequest();
        budgetRequest.setUserId(userId);
        budgetRequest.setCategory("FOOD");
        budgetRequest.setLimitAmount(10_000);
        budgetRequest.setStartDate(LocalDate.now());
        budgetRequest.setEndDate(LocalDate.now().plusDays(5));

        CreateBudgetResponse budget = budgetService.createBudget(budgetRequest);

        AddExpenseRequest expenseOne = new AddExpenseRequest();
        expenseOne.setUserId(userId);
        expenseOne.setAmount(3_000);
        expenseOne.setCategory("FOOD");
        expenseOne.setDate(LocalDate.now());
        expenseService.addExpense(expenseOne);

        CreateBudgetResponse updatedBudget = budgetService.getBudgetsByUser(userId).stream()
                .filter(b -> b.getCategory().equalsIgnoreCase("FOOD"))
                .findFirst()
                .orElseThrow();

        assertEquals(3_000, updatedBudget.getCurrentSpent());
        assertFalse(budgetService.isBudgetExceeded(updatedBudget.getId()));

        AddExpenseRequest expense2 = new AddExpenseRequest();
        expense2.setUserId(userId);
        expense2.setAmount(8_000);
        expense2.setCategory("FOOD");
        expense2.setDate(LocalDate.now());
        expenseService.addExpense(expense2);

        updatedBudget = budgetService.getBudgetsByUser(userId).stream()
                .filter(b -> b.getCategory().equalsIgnoreCase("FOOD"))
                .findFirst()
                .orElseThrow();

        assertEquals(11_000, updatedBudget.getCurrentSpent());
        assertTrue(budgetService.isBudgetExceeded(updatedBudget.getId()));
    }
}