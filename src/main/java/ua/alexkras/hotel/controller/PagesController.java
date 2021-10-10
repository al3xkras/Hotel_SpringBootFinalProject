package ua.alexkras.hotel.controller;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.alexkras.hotel.model.UserType;

@Controller
public class PagesController {

    @RequestMapping("/")
    public String mainPage(Model model){
        UserDetails user;

        try {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return "index";
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(UserType.USER.name()))){
            return "personal_area/user";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority(UserType.ADMIN.name()))){
            return "personal_area/admin";
        }

        return "index";
    }

}
