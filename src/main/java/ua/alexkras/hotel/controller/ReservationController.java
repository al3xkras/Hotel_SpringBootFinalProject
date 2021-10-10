package ua.alexkras.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.entity.Reservation;

@Controller
public class ReservationController {

    @GetMapping("/create_reservation")
    public String createReservationPage(Model model){
        model.addAttribute("reservationRequest",new Reservation());

        return "create_reservation";
    }

    @PostMapping("/create_reservation")
    public String submitReservationForm(
            @ModelAttribute("reservationRequest") Reservation reservation){

        System.out.println(reservation);

        return "redirect:/";
    }
}
