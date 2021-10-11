package ua.alexkras.hotel.model;

import java.text.SimpleDateFormat;

public interface MySqlStrings {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    String root = "jdbc:mysql://localhost:3306/";
    String databaseName="hotel_db";
    String tableUser = "user";

    String colUserId = "ID";
    String colUserName = "FIRST_NAME";
    String colUserSurname = "LAST_NAME";
    String colUserUsername = "USERNAME";
    String colUserPassword = "PASSWORD";
    String colUserPhoneNumber = "PHONE_NUMBER";
    String colUserBirthday = "BIRTHDAY";
    String colUserGender = "GENDER";
    String colUserUserType = "USER_TYPE";

    String[] tableUserColumns = new String[]{
        colUserId,colUserName, colUserSurname, colUserUsername,
                colUserPassword, colUserPhoneNumber, colUserBirthday,
                colUserGender, colUserUserType};

    String connectionUrl = root+databaseName+"?serverTimezone=UTC";

    String user = "root";
    String password = "root";


    String sqlCreateDatabaseIfNotExists = String.format("CREATE DATABASE IF NOT EXISTS %s;",
            databaseName);

    String sqlCreateUserTableIfNotExists = "CREATE TABLE IF NOT EXISTS "+
                    databaseName+"."+
                    tableUser+" ("+
                    colUserId+" INT AUTO_INCREMENT PRIMARY KEY UNIQUE, "+
                    colUserName+" VARCHAR(25),"+
                    colUserSurname+" VARCHAR(25), "+
                    colUserUsername+" VARCHAR(30) UNIQUE, "+
                    colUserPassword+" VARCHAR(100), "+
                    colUserPhoneNumber+" VARCHAR(20), "+
                    colUserBirthday+" DATE, "+
                    colUserGender+" VARCHAR(10), "+
                    colUserUserType+" VARCHAR(10)"+
                    ");";

    String sqlInsertIntoUserDB = "INSERT INTO " + databaseName + "." + tableUser +
            " ("+String.join(", ",tableUserColumns)+
            ") VALUES ( NULL, %s );";

    String sqlSelectColumnsFromUserDB = "SELECT %s FROM " + databaseName + "." + tableUser;

}
