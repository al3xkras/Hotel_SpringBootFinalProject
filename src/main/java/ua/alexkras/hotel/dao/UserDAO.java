package ua.alexkras.hotel.dao;

import ua.alexkras.hotel.entity.MySqlStrings;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.entity.UserType;

import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;


public class UserDAO {

    public static boolean addUser(User user) throws SQLException {
        Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);

        PreparedStatement addUserIfNotExists = conn.prepareStatement(
                String.format(MySqlStrings.sqlInsertIntoUserDB, user.toSqlString()));

        System.out.printf(MySqlStrings.sqlInsertIntoUserDB +"\n", user.toSqlString());
        boolean executeResult = addUserIfNotExists.execute();

        conn.close();

        return executeResult;
    }

    public static Optional<User> getUserByUsername(String username) throws SQLException{

        Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);

        PreparedStatement getUsersByUsername = conn.prepareStatement(
                String.format(MySqlStrings.sqlSelectColumnsFromUserDB,
                        String.join(", ",MySqlStrings.tableUserColumns))
                        +" WHERE " + MySqlStrings.colUserUsername + " = \"" + username + "\"" );

        ResultSet users = getUsersByUsername.executeQuery();

        if (!users.isBeforeFirst()){
            return Optional.empty();
        }
        users.next();

        User user = new User();

        user.setName(users.getString(MySqlStrings.colUserName));
        user.setSurname(users.getString(MySqlStrings.colUserSurname));
        user.setUsername(users.getString(MySqlStrings.colUserUsername));
        user.setPassword(users.getString(MySqlStrings.colUserPassword));
        user.setPhoneNumber(users.getString(MySqlStrings.colUserPhoneNumber));

        String birthdayStr = users.getString(MySqlStrings.colUserBirthday);

        try {
            LocalDate birthdayDate = MySqlStrings.dateFormat
                            .parse(birthdayStr)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

            user.setBirthday(birthdayDate);

        } catch (ParseException e){
            e.printStackTrace();
            System.out.printf("Cannot parse Date string \"%s\" using %s\n",birthdayStr,MySqlStrings.dateFormat);
        }

        user.setGender(users.getString(MySqlStrings.colUserGender));

        String userTypeStr = users.getString(MySqlStrings.colUserUserType);
        UserType userType = Arrays.stream(UserType.values())
                .filter(x->x.name().equalsIgnoreCase(userTypeStr))
                .findFirst().orElse(UserType.USER);

        user.setUserType(userType);

        conn.close();

        return Optional.of(user);
    }



}
