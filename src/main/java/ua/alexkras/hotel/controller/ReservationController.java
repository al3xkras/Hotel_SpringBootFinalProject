package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.service.ReservationService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ReservationController {

    private  final ReservationService reservationService;
    private final AuthController authController;

    @Autowired
    public ReservationController(ReservationService reservationService,
                                 AuthController authController){
        this.reservationService=reservationService;
        this.authController=authController;
    }


    @GetMapping("/create_reservation")
    public String createReservationPage(Model model){
        model.addAttribute("reservationRequest",new Reservation());

        return "create_reservation";
    }

    @PostMapping("/create_reservation")
    public String submitReservationForm(
            @ModelAttribute("reservationRequest") @Valid Reservation reservation,
            Model model){

        Optional<User> optionalUser = authController.getCurrentUser();

        if (!optionalUser.isPresent()){
            return "redirect:/";
        }

        User currentUser = optionalUser.get();

        if (reservation.getFromDate().compareTo(reservation.getToDate())>=0){
            model.addAttribute("fromDateIsGreaterThanToDate",true);
            return "create_reservation";
        }

        reservation.setUserId(currentUser.getId());
        reservation.setSubmitDate(LocalDateTime.now());

        System.out.println(reservation);

        reservationService.addReservation(reservation);

        return "redirect:/";
    }
}
