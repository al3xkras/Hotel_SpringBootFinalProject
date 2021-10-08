package ua.alexkras.hotel.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.alexkras.hotel.model.UserType;

@Controller
public class PagesController {

    @RequestMapping("/")
    @PreAuthorize("permitAll()")
    public String mainPage(){
        return "index";
    }

    @GetMapping("/personal_area")
    public String personalAreaPage(){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(UserType.USER.name()))){
            return "personal_area/user";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority(UserType.ADMIN.name()))){
            return "personal_area/admin";
        }

        return "/index";
    }

}
