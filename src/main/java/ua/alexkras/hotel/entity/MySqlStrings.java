package ua.alexkras.hotel.entity;

public interface MySqlStrings {
    String databaseName="hotel_db";
    String tableUser = "user";

    String connectionUrl = String.format("jdbc:mysql://localhost:3306/%s?serverTimezone=UTC",databaseName);

    String user = "root";
    String password = "root";


    String sqlCreateDatabaseIfNotExists = String.format("CREATE DATABASE IF NOT EXISTS %s;",
            databaseName);

    String sqlCreateUserTableIfNotExists = String.format("CREATE TABLE IF NOT EXISTS %s.%s (ID INT AUTO_INCREMENT PRIMARY KEY, FIRST_NAME VARCHAR(50),LAST_NAME VARCHAR(50), USERNAME VARCHAR(50) unique, PASSWORD VARCHAR(50), PHONE_NUMBER VARCHAR(50), BIRTHDAY VARCHAR(50), GENDER VARCHAR(10));",
            databaseName, tableUser);

    String sqlInsertIntoUserDB = "INSERT INTO " + databaseName + "." + tableUser +
            " (ID, FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, PHONE_NUMBER, BIRTHDAY, GENDER) VALUES ( NULL, %s );";


}
