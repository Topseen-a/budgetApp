package com.budgetApp.services;

import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.EmailAlreadyExistsException;
import com.budgetApp.exceptions.InvalidEmailException;
import com.budgetApp.exceptions.InvalidNameException;
import com.budgetApp.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testThatA_newUserIsCreated() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        assertEquals("Oluwaseun", response.getName());
        assertEquals("oluwaseun@gmail.com", response.getEmail());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testThatA_userIsCreatedWithAnEmptyNameThrowsAnError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        assertThrows(InvalidNameException.class, () -> userService.createUser(request));
    }

    @Test
    public void testThatA_userIsCreatedWithAnInvalidEmailThrowsAnError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun.com");
        request.setPassword("oluwaseun1234");

        assertThrows(InvalidEmailException.class, () ->  userService.createUser(request));
    }

    @Test
    public void testThatAnotherUserIsCreatedWithTheSameEmailThrowsAnError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        userService.createUser(request);

        CreateUserRequest duplicate = new CreateUserRequest();
        duplicate.setName("Adedayo");
        duplicate.setEmail("oluwaseun@gmail.com");
        duplicate.setPassword("adedayo1234");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(duplicate));
    }

    @Test
    public void testThatA_userWithAnInvalidPasswordThrowsAnError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("seun");

        assertThrows(InvalidPasswordException.class, () -> userService.createUser(request));
    }

    @Test
    public void testThatMultipleUsersCanBeCreated() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPassword("oluwaseun1234");

        userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Adedayo");
        requestTwo.setEmail("adedayo@gmail.com");
        requestTwo.setPassword("adedayo1234");

        userService.createUser(requestTwo);

        assertEquals(2, userRepository.count());
    }


}
