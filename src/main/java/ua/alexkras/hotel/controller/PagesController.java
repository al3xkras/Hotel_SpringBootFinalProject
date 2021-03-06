package ua.alexkras.hotel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.UserType;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;
import java.util.Optional;

@Controller
public class PagesController implements ErrorController {
    private final ReservationService reservationService;
    private final AuthController authController;

    @Autowired
    public PagesController(ReservationService reservationService,
                           AuthController authController,
                           ApartmentService apartmentService){
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

    @RequestMapping("/error")
    public String handleUnexpectedError() {
        return "error_page";
    }

    @ExceptionHandler(RuntimeException.class)
    public String databaseError(Model model) {
        return "error_page";
    }
}
