package ua.alexkras.hotel.controller;


import org.springframework.beans.factory.annotation.Autowired;
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
    private final ReservationService reservationService;

    @Autowired
    public PagesController(ReservationService reservationService){
        this.reservationService=reservationService;
    }


    @RequestMapping("/")
    public String mainPage(Model model){

        Optional<User> optionalUser = Optional.ofNullable(UserDAO.getCurrentUser());

        if (!optionalUser.isPresent()){
            return "index";
        }

        User user = optionalUser.get();

        if (user.getUserType().equals(UserType.USER)){
            model.addAttribute("allReservations",
                    reservationService.getReservationsByUserId(user.getId()));

            return "personal_area/user";
        } else if (user.getUserType().equals(UserType.ADMIN)){
            model.addAttribute("allReservations",
                    reservationService.getAllReservations());

            return "personal_area/admin";
        }

        return "index";
    }

}
