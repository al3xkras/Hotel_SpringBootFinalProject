package ua.alexkras.hotel.controller;




import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.CustomUserDetailsService;
import ua.alexkras.hotel.service.UserService;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

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
        User currentUser =  CustomUserDetailsService.getCurrentUser();

        if (currentUser!=null) {
            return Optional.of(currentUser);
        }

        UserDetails springSecurityUser;

        try {
            springSecurityUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return Optional.empty();
        }

        return userService.getUserByUserName(springSecurityUser.getUsername());
    }

}
