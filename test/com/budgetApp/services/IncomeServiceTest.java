package com.budgetApp.services;

import com.budgetApp.data.repositories.IncomeRepository;
import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.AddIncomeRequest;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.AddIncomeResponse;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.IncomeNotFoundException;
import com.budgetApp.exceptions.InvalidRequestException;
import com.budgetApp.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class IncomeServiceTest {

    @Autowired
    private IncomeService incomeService;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserService  userService;

    @Autowired
    private UserRepository userRepository;

    private String userId;

    @BeforeEach
    public void setup() {
        incomeRepository.deleteAll();
        userRepository.deleteAll();

        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);
        userId = response.getId();
    }

    @Test
    public void testThatIncomeCanBeAdded() {
        AddIncomeRequest request =  new AddIncomeRequest();
        request.setUserId(userId);
        request.setAmount(5_000);
        request.setSource("Salary");
        request.setDate(LocalDate.now());

        AddIncomeResponse response = incomeService.addIncome(request);

        assertEquals(1, incomeRepository.count());
        assertEquals(5_000, response.getAmount());
        assertEquals("Salary", response.getSource());
    }

    @Test
    public void testThatAddingIncomeWithInvalidUserThrowsException() {
        AddIncomeRequest request = new AddIncomeRequest();
        request.setUserId("123-456");
        request.setAmount(5_000);
        request.setSource("Salary");
        request.setDate(LocalDate.now());

        assertThrows(UserNotFoundException.class, () -> incomeService.addIncome(request));
    }

    @Test
    public void testThatAddingIncomeWithInvalidAmountThrowsException() {
        AddIncomeRequest request = new AddIncomeRequest();
        request.setUserId(userId);
        request.setAmount(0);
        request.setSource("Salary");
        request.setDate(LocalDate.now());

        assertThrows(InvalidRequestException.class, () -> incomeService.addIncome(request));
    }

    @Test
    public void testThatIncomeCanBeGottenById() {
        AddIncomeRequest request = new AddIncomeRequest();
        request.setUserId(userId);
        request.setAmount(5_000);
        request.setSource("Hustle");
        request.setDate(LocalDate.now());

        AddIncomeResponse response = incomeService.addIncome(request);

        AddIncomeResponse found = incomeService.getIncomeById(response.getId());

        assertEquals("Hustle", found.getSource());
    }

    @Test
    public void testThatGettingInvalidIncomeThrowsException() {
        assertThrows(IncomeNotFoundException.class, () -> incomeService.getIncomeById("123-456"));
    }

    @Test
    public void testThatUserCanHaveMultipleIncomes() {
        AddIncomeRequest requestOne = new AddIncomeRequest();
        requestOne.setUserId(userId);
        requestOne.setAmount(5_000);
        requestOne.setSource("Salary");
        requestOne.setDate(LocalDate.now());

        incomeService.addIncome(requestOne);

        AddIncomeRequest requestTwo = new AddIncomeRequest();
        requestTwo.setUserId(userId);
        requestTwo.setAmount(10_000);
        requestTwo.setSource("Hustle");
        requestTwo.setDate(LocalDate.now());

        incomeService.addIncome(requestTwo);

        assertEquals(2, incomeService.getAllIncomesByUser(userId).size());
    }

    @Test
    public void testThatIncomeCanBeUpdated() {
        AddIncomeRequest request = new AddIncomeRequest();
        request.setUserId(userId);
        request.setAmount(5_000);
        request.setSource("Old Source");
        request.setDate(LocalDate.now());

        AddIncomeResponse response = incomeService.addIncome(request);

        AddIncomeRequest update = new AddIncomeRequest();
        update.setUserId(userId);
        update.setAmount(8_000);
        update.setSource("New Source");
        update.setDate(LocalDate.now());

        AddIncomeResponse updated = incomeService.updateIncome(response.getId(), update);

        assertEquals(8_000, updated.getAmount());
        assertEquals("New Source", updated.getSource());
    }

    @Test
    public void testThatUpdatingInvalidIncomeThrowsException() {
        AddIncomeRequest request = new AddIncomeRequest();
        request.setUserId(userId);
        request.setAmount(5_000);
        request.setSource("Salary");
        request.setDate(LocalDate.now());

        assertThrows(IncomeNotFoundException.class, () -> incomeService.updateIncome("123-456", request));
    }

    @Test
    public void testThatIncomeCanBeDeleted() {
        AddIncomeRequest request = new AddIncomeRequest();
        request.setUserId(userId);
        request.setAmount(5_000);
        request.setSource("Salary");
        request.setDate(LocalDate.now());

        AddIncomeResponse response = incomeService.addIncome(request);

        incomeService.deleteIncome(response.getId());

        assertEquals(0, incomeRepository.count());
    }

    @Test
    public void testThatDeletingInvalidIncomeThrowsException() {
        assertThrows(IncomeNotFoundException.class, () -> incomeService.deleteIncome("123-456"));
    }
}
