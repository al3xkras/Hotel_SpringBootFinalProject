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
    private final AuthController authController;

    @Autowired
    public PagesController(ReservationService reservationService,
                           AuthController authController){
        this.reservationService=reservationService;
        this.authController=authController;
    }


    @RequestMapping("/")
    public String mainPage(){

        Optional<User> optionalUser = authController.getCurrentUser();

        if (!optionalUser.isPresent()){
            return "index";
        }

        if (optionalUser.get()
                .getUserType()
                .equals(UserType.USER)){

            return "redirect:/user";
        } else if (optionalUser.get()
                .getUserType()
                .equals(UserType.ADMIN)){

            return "redirect:/admin";
        }

        return "index";
    }

    @GetMapping("/user")
    public String userMainPage(Model model){
        Optional<User> optionalUser = authController.getCurrentUser();

        if (!optionalUser.isPresent()){
            return "index";
        }

        model.addAttribute("allReservations",
                reservationService.getReservationsByUserId(
                        optionalUser.get().getId()));

        return "personal_area/user";
    }


    @GetMapping("/admin")
    public String adminMainPage(Model model){

        model.addAttribute("allReservations",
                reservationService.getAllReservations());

        return "personal_area/admin";
    }

}
