package com.budgetApp.services;

import com.budgetApp.data.repositories.UserRepository;
import com.budgetApp.dtos.requests.CreateUserRequest;
import com.budgetApp.dtos.responses.CreateUserResponse;
import com.budgetApp.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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

    @Test
    public void testThatFindAllUsersReturnAllUsers() {
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

        List<CreateUserResponse> allUsers = userService.getAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    public void testThatA_userCanBeGottenById() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPassword("oluwaseun1234");

        CreateUserResponse responseOne = userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Adedayo");
        requestTwo.setEmail("adedayo@gmail.com");
        requestTwo.setPassword("adedayo1234");

        CreateUserResponse responseTwo = userService.createUser(requestTwo);

        CreateUserResponse foundUser = userService.getUserById(responseOne.getId());

        assertEquals(responseOne, foundUser);
    }

    @Test
    public void testThatGettingUserWithInvalidIdThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        userService.createUser(request);

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("123-456"));
    }

    @Test
    public void testThatA_userCanBeGottenByEmail() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPassword("oluwaseun1234");

        CreateUserResponse responseOne = userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Adedayo");
        requestTwo.setEmail("adedayo@gmail.com");
        requestTwo.setPassword("adedayo1234");

        CreateUserResponse responseTwo = userService.createUser(requestTwo);

        CreateUserResponse foundUser = userService.getUserByEmail(responseOne.getEmail());

        assertEquals(responseOne, foundUser);
    }

    @Test
    public void testThatGettingUserWithInvalidEmailThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        userService.createUser(request);

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("seun@gmail.com"));
    }

    @Test
    public void testThatA_userCanBeUpdated() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        CreateUserRequest updatedUser = new CreateUserRequest();
        updatedUser.setName("Adedayo");
        updatedUser.setEmail("oluwaseun@gmail.com");
        updatedUser.setPassword("adedayo1234");

        CreateUserResponse updatedResponse = userService.updateUser(response.getId(), updatedUser);

        assertEquals("Adedayo", updatedResponse.getName());
        assertEquals("oluwaseun@gmail.com", updatedResponse.getEmail());
    }

    @Test
    public void testThatUpdatingNonExistentUserThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        assertThrows(UserNotFoundException.class, () -> userService.updateUser("123-456", request));
    }

    @Test
    public void testThatUpdatingToExistingEmailThrowsAnError() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPassword("oluwaseun1234");

        CreateUserResponse responseOne = userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Adedayo");
        requestTwo.setEmail("adedayo@gmail.com");
        requestTwo.setPassword("adedayo1234");

        CreateUserResponse responseTwo = userService.createUser(requestTwo);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Ajayi");
        update.setEmail("oluwaseun@gmail.com");
        update.setPassword("ajayi1234");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(responseTwo.getId(), update));
    }

    @Test
    public void testThatUpdatingWithInvalidEmailThrowsError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Oluwaseun");
        update.setEmail("seungmail.com");
        update.setPassword("oluwaseun1234");

        assertThrows(InvalidEmailException.class, () -> userService.updateUser(response.getId(), update));
    }

    @Test
    public void testThatUpdatingWithEmptyNameThrowsError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("");
        update.setEmail("oluwaseun@gmail.com");
        update.setPassword("oluwaseun1234");

        assertThrows(InvalidNameException.class, () -> userService.updateUser(response.getId(), update));
    }

    @Test
    public void testThatUpdatingWithShortPasswordThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Oluwaseun");
        update.setEmail("oluwaseun@gmail.com");
        update.setPassword("123");

        assertThrows(InvalidPasswordException.class, () -> userService.updateUser(response.getId(), update));
    }

    @Test
    public void testThatUpdatingWithSameEmailWorks() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        CreateUserRequest update = new CreateUserRequest();
        update.setName("Ajayi");
        update.setEmail("oluwaseun@gmail.com");
        update.setPassword("ajayi1234");

        CreateUserResponse updated = userService.updateUser(response.getId(), update);

        assertEquals("Ajayi", updated.getName());
    }

    @Test
    public void testThatA_userCanBeDeleted() {
        CreateUserRequest requestOne = new CreateUserRequest();
        requestOne.setName("Oluwaseun");
        requestOne.setEmail("oluwaseun@gmail.com");
        requestOne.setPassword("oluwaseun1234");

        CreateUserResponse responseOne = userService.createUser(requestOne);

        CreateUserRequest requestTwo = new CreateUserRequest();
        requestTwo.setName("Ajayi");
        requestTwo.setEmail("ajayi@gmail.com");
        requestTwo.setPassword("ajayi1234");

        CreateUserResponse responseTwo = userService.createUser(requestTwo);

        userService.deleteUser(responseTwo.getId());

        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    public void testThatDeletingA_taskThatDoesNotExistThrowsError() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Oluwaseun");
        request.setEmail("oluwaseun@gmail.com");
        request.setPassword("oluwaseun1234");

        CreateUserResponse response = userService.createUser(request);

        userService.deleteUser(response.getId());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(response.getId()));
    }
}
