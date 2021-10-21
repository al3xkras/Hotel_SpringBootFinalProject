package ua.alexkras.hotel.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.alexkras.hotel.entity.User;

public class CustomUserDetailsService implements UserDetailsService {

    private static User currentUser;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        if (currentUser==null){
            throw new UsernameNotFoundException(name);
        }

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(currentUser.getUsername())
                .password(passwordEncoder().encode(currentUser.getPassword()))
                .authorities(
                        new SimpleGrantedAuthority(currentUser.getUserType().name())
                )
                .build();
    }

    public static void clearCurrentUser(){
        currentUser=null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void updateCurrentUserByUsername(){

    }

    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

}
