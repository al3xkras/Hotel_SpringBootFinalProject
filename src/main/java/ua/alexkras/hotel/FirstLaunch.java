package ua.alexkras.hotel;

import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.MySqlStrings;
import ua.alexkras.hotel.model.UserType;

import java.sql.*;
import java.time.LocalDate;

public class FirstLaunch {
    public static void main(String[] args) {
        //Create database if not exists before starting Spring Boot application
        try (Connection conn = DriverManager.getConnection(MySqlStrings.root, MySqlStrings.user, MySqlStrings.password);
             PreparedStatement createDB = conn.prepareStatement(MySqlStrings.sqlCreateDatabaseIfNotExists);
             PreparedStatement createUserTable = conn.prepareStatement(MySqlStrings.sqlCreateUserTableIfNotExists)
        ) {
            createDB.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to establish MqSQL connection and create database: "+MySqlStrings.databaseName);
        }
        /*
        try {
            UserDAO.addUser(
                    new User("Admin", "0",
                            "Admin1", "password1",
                            "+404-23-4567890",
                            LocalDate.parse("2002-03-07")
                            , "Male", UserType.ADMIN)
            );

            UserDAO.addUser(
                    new User("MyName", "MySurname",
                            "Admin2", "password2",
                            "+404-12-3456789",
                            LocalDate.parse("2002-03-07")
                            , "Male", UserType.ADMIN)
            );
        }catch (SQLIntegrityConstraintViolationException ignored){

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Cannot add Admin accounts to hotel's database.");
        }

         */
    }
}
