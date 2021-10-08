package ua.alexkras.hotel.dao;
import ua.alexkras.hotel.entity.MySqlStrings;
import ua.alexkras.hotel.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UserDAO {

    public static boolean addUser(User user) throws SQLException {
        Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);

        PreparedStatement addUserIfNotExists = conn.prepareStatement(
                String.format(MySqlStrings.sqlInsertIntoUserDB, user.toSqlString()));

        System.out.printf(MySqlStrings.sqlInsertIntoUserDB, user.toSqlString());
        boolean executeResult = addUserIfNotExists.execute();

        conn.close();

        return executeResult;
    }

}
