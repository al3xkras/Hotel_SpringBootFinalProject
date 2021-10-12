package ua.alexkras.hotel.controller;


import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.dao.UserDAO;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String getLoginPage(){
        return "/auth/login";
    }

    @PostConstruct
    protected void getUserFromSession(){
        try{
            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            UserDAO.getUserByUsername(user.getUsername())
                    .ifPresent(UserDAO::setCurrentUser);

        } catch (Exception ignored){

        }
    }
}
