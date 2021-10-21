package ua.alexkras.hotel.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.sql.*;
import java.util.Optional;


@Slf4j
public class HotelUserDetailsService implements UserDetailsService {

    private static UserDetails currentUserDetails;

    public static Optional<UserDetails> getUserDetailsFromUserByUsername(String username){
        if (currentUserDetails !=null && currentUserDetails.getUsername().equalsIgnoreCase(username)){
            return Optional.of(currentUserDetails);
        }

        try(Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);
            PreparedStatement getUsersByUsername = conn.prepareStatement(
                    String.format(MySqlStrings.sqlSelectColumnsFromUserDB,
                            String.join(", ",MySqlStrings.tableUserColumns))
                            +" WHERE " + MySqlStrings.colUserUsername + " = \"" + username + "\"" );){
            ResultSet users = getUsersByUsername.executeQuery();

            if (!users.isBeforeFirst()){
                return Optional.empty();
            }
            users.next();

            currentUserDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(users.getString(MySqlStrings.colUserUsername))
                    .password(users.getString(MySqlStrings.colUserPassword))
                    .authorities(
                            new SimpleGrantedAuthority(
                                    Optional.of(UserType.valueOf(users.getString(MySqlStrings.colUserUserType))).orElse(UserType.USER).name()
                            )
                    )
                    .build();
        } catch (SQLException e){
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(currentUserDetails);
    }

    public static void clearCurrentUserDetails() {
        currentUserDetails=null;
    }

    public static void updateCurrentUserDetails(String username) {
        currentUserDetails = getUserDetailsFromUserByUsername(username).orElseThrow(IllegalStateException::new);
    }

    public static UserDetails getCurrentUser() {
        return currentUserDetails;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserDetailsFromUserByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
    }

    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}

