package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.service.ReservationService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

        return "/reservation/create_reservation";
    }

    @PostMapping("/create_reservation")
    public String submitReservationForm(
            @ModelAttribute("reservationRequest") @Valid Reservation reservation,
            Model model){

        User currentUser = authController.getCurrentUser().orElseThrow(IllegalStateException::new);

        if (reservation.getFromDate().compareTo(reservation.getToDate())>=0){
            model.addAttribute("fromDateIsGreaterThanToDate",true);
            return "/reservation/create_reservation";
        }

        reservation.setUserId(currentUser.getId());
        reservation.setSubmitDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        reservation.setReservationStatus(ReservationStatus.PENDING);

        System.out.println(reservation);

        reservationService.create(reservation);

        return "redirect:/";
    }
}
