package ua.alexkras.hotel.controller;




import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.entity.User;

import java.sql.SQLException;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String getLoginPage(){
        return "/auth/login";
    }

    @Bean
    public Optional<User> getCurrentUser(){
        User currentUser =  UserDAO.getCurrentUser();

        if (currentUser!=null)
            return Optional.of(currentUser);

        UserDetails springSecurityUser;

        try {
            springSecurityUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return Optional.empty();
        }

        Optional<User> optionalUser = Optional.empty();

        try {
            optionalUser =  UserDAO.getUserByUsername(springSecurityUser.getUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return optionalUser;
    }

}
