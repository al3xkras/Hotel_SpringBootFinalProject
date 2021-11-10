package ua.alexkras.hotel.hotel.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.hotel.test_db_connection.TestDatabase;
import ua.alexkras.hotel.model.UserType;
import ua.alexkras.hotel.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    private final UserService userService;
    private final TestDatabase testDatabase;

    static User testUser3 = User.builder()
            .id(3L)
            .name("Admin").surname("Adminovich")
            .username("someUsername").password("password1")
            .phoneNumber("+404-23-4567890")
            .birthday(LocalDate.parse("2002-03-07"))
            .gender("Male").userType(UserType.ADMIN).build();

    @Autowired
    public UserServiceTest(UserService userService, TestDatabase testDatabase) {
        this.userService = userService;
        this.testDatabase = testDatabase;
    }

    @BeforeTestClass
    public void beforeClass() {
        testDatabase.createTestDatabase();
    }

    @BeforeEach
    public void beforeTest(){
        testDatabase.createTestDatabase();
    }

    @Test
    public void testFindByUsername1(){
        User testUser = TestDatabase.testUser1;

        User actual = userService.findByUsername(testUser.getUsername())
                .orElseThrow(IllegalStateException::new);

        assertEquals(testUser,actual);
    }

    @Test(expected=IllegalStateException.class)
    public void testFindByUsername2(){
        userService.findByUsername("someUnknownUsername")
                .orElseThrow(IllegalStateException::new);
    }

    @Test
    public void testCreate1(){
        userService.create(testUser3);

        User actual = userService.findByUsername(testUser3.getUsername())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(testUser3,actual);
    }

    @Test(expected = RuntimeException.class)
    public void testCreate2(){
        userService.create(testUser3);
        userService.create(testUser3);
    }


}