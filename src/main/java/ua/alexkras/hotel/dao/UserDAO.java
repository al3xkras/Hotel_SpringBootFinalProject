package ua.alexkras.hotel.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.alexkras.hotel.model.MySqlStrings;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.UserType;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;


@Slf4j
public class UserDAO implements UserDetailsService {

    private static User currentUser;

    public static void addUser(User user) throws SQLException {
        Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);

        user.setPassword(passwordEncoder().encode(user.getPassword()));

        PreparedStatement addUserIfNotExists = conn.prepareStatement(
                String.format(MySqlStrings.sqlInsertIntoUserDB, user.toSqlString()));


        log.info(String.format(MySqlStrings.sqlInsertIntoUserDB +"\n", user.toSqlString()));

        addUserIfNotExists.execute();

        conn.close();
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
        user.setId(users.getInt(MySqlStrings.colUserId));
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
            log.info(String.format("Cannot parse Date string \"%s\" using %s\n",birthdayStr,MySqlStrings.dateFormat));
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


    public static Integer getUserIdByUsername(String username) throws SQLException {
        Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);

        PreparedStatement getUsersByUsername = conn.prepareStatement(
                String.format(MySqlStrings.sqlSelectColumnsFromUserDB,
                        MySqlStrings.colUserId)
                        +" WHERE " + MySqlStrings.colUserUsername + " = \"" + username + "\"" );

        ResultSet users = getUsersByUsername.executeQuery();

        users.next();

        Integer id = users.getInt(MySqlStrings.colUserId);

        conn.close();

        return id;
    }


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> optionalUser;

        try {
            optionalUser = getUserByUsername(name);
        } catch (SQLException e){
            e.printStackTrace();
            throw new UsernameNotFoundException(name);
        }

        if (!optionalUser.isPresent()) throw new UsernameNotFoundException(name);

        User user = optionalUser.get();

        currentUser = user;

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        new SimpleGrantedAuthority(user.getUserType().name())
                )
                .build();
    }

    private static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
