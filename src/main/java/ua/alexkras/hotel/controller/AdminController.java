package ua.alexkras.hotel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final ApartmentService apartmentService;

    @Autowired
    public AdminController(ReservationService reservationService,
                           ApartmentService apartmentService){
        this.reservationService=reservationService;
        this.apartmentService=apartmentService;
    }

    @GetMapping
    public String adminMainPage(Model model){

        model.addAttribute("pendingReservations",
                reservationService.getPendingReservations());

        return "personal_area/admin";
    }

    @GetMapping("/reservation/{id}")
    public String pendingReservationPage(@PathVariable Integer id, Model model){
        if (id==null){
            return "redirect:/admin";
        }

        Optional<Reservation> optionalReservation = reservationService.getReservationById(id);

        if (!optionalReservation.isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        model.addAttribute("reservation",reservation);

        model.addAttribute("matchingApartments",
                apartmentService.findApartmentsMatchingReservation(reservation));

        return "/reservation/reservation";
    }



}
