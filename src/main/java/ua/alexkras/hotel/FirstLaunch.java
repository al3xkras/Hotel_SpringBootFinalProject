package ua.alexkras.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.HotelUserDetailsService;
import ua.alexkras.hotel.model.MySqlStrings;
import ua.alexkras.hotel.model.UserType;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.time.LocalDate;


public class FirstLaunch {
    public static void main(String[] args) {
        //Create database if not exists before starting Spring Boot application
        try (Connection conn = DriverManager.getConnection(MySqlStrings.root, MySqlStrings.user, MySqlStrings.password);
             PreparedStatement createDB = conn.prepareStatement(MySqlStrings.sqlCreateDatabaseIfNotExists);
             PreparedStatement createUserTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                     "hotel_db.user (ID INT PRIMARY KEY UNIQUE)")
        ) {
            createDB.execute();
            createUserTable.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to establish MqSQL connection and create database: "+MySqlStrings.databaseName);
        }

        SpringApplication.run(HotelApplication.class, args);

        addUser(-1L,new User("Admin", "0",
                "Admin1", "password1",
                "+404-23-4567890",
                LocalDate.parse("2002-03-07"),
                "Male", UserType.ADMIN));

        addUser(-2L,new User("MyName", "MySurname",
                "Admin2", "password2",
                "+404-12-3456789",
                LocalDate.parse("2002-03-07"),
                "Male", UserType.ADMIN));
    }


    public static void addUser(long id, User user) {
        PasswordEncoder encoder = HotelUserDetailsService.passwordEncoder();
        String passwordNotEncoded=user.getPassword();

        try(Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);
            PreparedStatement addUserIfNotExists = conn.prepareStatement("INSERT INTO hotel_db.user " +
                    "(id, birthday, gender, name, password, phone_number, surname, user_type, username)" +
                    " VALUES " +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        ){

            user.setPassword(encoder.encode(user.getPassword()));

            addUserIfNotExists.setLong(1,id);
            addUserIfNotExists.setDate(2, Date.valueOf(user.getBirthday()));
            addUserIfNotExists.setString(3, user.getGender());
            addUserIfNotExists.setString(4, user.getName());
            addUserIfNotExists.setString(5, user.getPassword());
            addUserIfNotExists.setString(6, user.getPhoneNumber());
            addUserIfNotExists.setString(7, user.getSurname());
            addUserIfNotExists.setString(8, user.getUserType().name());
            addUserIfNotExists.setString(9, user.getUsername());

            addUserIfNotExists.execute();

        } catch (SQLIntegrityConstraintViolationException ignored){

        }catch (SQLException e){
            user.setPassword(passwordNotEncoded);
            e.printStackTrace();
        }
    }
}
