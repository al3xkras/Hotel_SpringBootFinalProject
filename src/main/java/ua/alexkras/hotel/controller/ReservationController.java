package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.service.ReservationService;
import javax.validation.Valid;
import java.sql.SQLException;

@Controller
public class ReservationController {

    private ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService=reservationService;
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

        User currentUser = (User)SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();

        try {
            reservation.setUserId(UserDAO.getUserIdByUsername(currentUser.getUsername()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (reservation.getFromDate().compareTo(reservation.getToDate())>=0){
            model.addAttribute("fromDateIsGreaterThanToDate",true);
            return "create_reservation";
        }

        System.out.println(reservation);

        reservationService.addReservation(reservation);

        return "redirect:/";
    }
}
