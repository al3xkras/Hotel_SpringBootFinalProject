package ua.alexkras.hotel.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.model.HotelUserDetailsService;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.service.UserService;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static User currentUser;
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/auth/login";
    }

    @Bean
    public Optional<User> getCurrentUser(){
        UserDetails springSecurityUser;

        try {
            springSecurityUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return Optional.empty();
        }

        if (HotelUserDetailsService.getCurrentUser()==null){
            HotelUserDetailsService.updateCurrentUserDetails(springSecurityUser.getUsername());
        }

        UserDetails currentUserDetails = HotelUserDetailsService.getCurrentUser();

        if (currentUser!=null && currentUser.getUsername()
                .equalsIgnoreCase(currentUserDetails.getUsername())) {
            return Optional.of(currentUser);
        }

        updateCurrentUser(currentUserDetails.getUsername());

        return Optional.of(currentUser);
    }

    private void updateCurrentUser(String username) {
        if (currentUser!=null && currentUser.getUsername().equalsIgnoreCase(username)){
            return;
        }
        currentUser = userService.getUserByUserName(username).orElseThrow(IllegalStateException::new);
    }

    public void clearCurrentUser(){
        currentUser=null;
    }

}
