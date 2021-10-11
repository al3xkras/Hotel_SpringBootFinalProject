package ua.alexkras.hotel.controller;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.UserType;
import ua.alexkras.hotel.service.ReservationService;

import java.util.Optional;

@Controller
public class PagesController {
    private ReservationService reservationService;

    public PagesController(ReservationService reservationService){
        this.reservationService=reservationService;
    }

    @RequestMapping("/")
    public String mainPage(Model model){
        UserDetails user;
        int currentUserId;

        try {
            user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            currentUserId = UserDAO.getUserIdByUsername(user.getUsername());
        } catch (Exception e){
            return "index";
        }



        if (user.getAuthorities().contains(new SimpleGrantedAuthority(UserType.USER.name()))){
            model.addAttribute("allReservations",
                    reservationService.getReservationsByUserId(currentUserId));

            return "personal_area/user";
        } else if (user.getAuthorities().contains(new SimpleGrantedAuthority(UserType.ADMIN.name()))){
            model.addAttribute("allReservations",
                    reservationService.getAllReservations());

            return "personal_area/admin";
        }

        return "index";
    }

}
