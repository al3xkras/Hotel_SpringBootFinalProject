package ua.alexkras.hotel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.service.ReservationService;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final AuthController authController;
    private final ReservationService reservationService;

    @Autowired
    public UserController(AuthController authController,
                          ReservationService reservationService){
        this.authController=authController;
        this.reservationService=reservationService;
    }

    @GetMapping
    public String userMainPage(Model model){
        Optional<User> optionalUser = authController.getCurrentUser();

        if (!optionalUser.isPresent()){
            return "index";
        }

        model.addAttribute("allReservations",
                reservationService.getActiveReservationsByUserId(
                        optionalUser.get().getId()));

        return "personal_area/user";
    }

    @GetMapping("/reservation/{id}")
    public String reservationFromTable(@PathVariable("id") Integer reservationId,
                                       Model model){
        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !(optionalReservation=reservationService.getReservationById(reservationId)).isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (!reservation.isCompleted()){
            return "redirect:/";
        }

        model.addAttribute("reservation",reservation);

        model.addAttribute("isCompleted",true);
        model.addAttribute("userAccount",true);

        return "/reservation/reservation";
    }

    @PostMapping("/reservation/{id}/confirm")
    public String confirmReservation(@PathVariable("id") Integer reservationId){

        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !(optionalReservation=reservationService.getReservationById(reservationId)).isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (!authController.getCurrentUser().isPresent() ||
                reservation.getUserId()!=authController.getCurrentUser().get().getId()){
            return "redirect:/";
        }


        reservation.setReservationStatus(ReservationStatus.RESERVED);

        if (!reservationService.updateReservationStatusById(reservationId,ReservationStatus.RESERVED)){
            return "redirect:/error";
        }

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/cancel")
    public String cancelReservation(@PathVariable("id") Integer reservationId) {
        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !(optionalReservation=reservationService.getReservationById(reservationId)).isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (!authController.getCurrentUser().isPresent() ||
                reservation.getUserId()!=authController.getCurrentUser().get().getId()){
            return "redirect:/";
        }


        reservation.setReservationStatus(ReservationStatus.CANCELLED);

        if (!reservationService.updateReservationStatusById(reservationId,ReservationStatus.CANCELLED)){
            return "redirect:/error";
        }

        return "redirect:/";
    }
}
