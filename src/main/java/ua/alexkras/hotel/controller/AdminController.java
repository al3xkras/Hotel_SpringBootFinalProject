package ua.alexkras.hotel.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
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

        reservationService.updateCurrentPendingReservations();

        model.addAttribute("pendingReservations",
                reservationService.getCurrentPendingReservations());

        return "personal_area/admin";
    }

    @GetMapping("/reservation/{id}")
    public String pendingReservationPage(@PathVariable Integer id, Model model){
        reservationService.updateCurrentReservation(id);

        model.addAttribute("reservation",reservationService.getCurrentReservation());

        boolean isCompleted = reservationService.getCurrentReservation().isCompleted();
        model.addAttribute("isCompleted", isCompleted);

        if (!isCompleted) {
            apartmentService.updateApartmentsMatchingCurrentReservation();
            model.addAttribute("matchingApartments", apartmentService.getApartmentsMatchingCurrentReservation());
        }

        return "/reservation/reservation";
    }

    @GetMapping("/reservation/{id}/select/{apartmentId}")
    public String confirmReservationPage(@PathVariable("id") Integer reservationId,
                                         @PathVariable("apartmentId") Integer apartmentId,
                                         Model model){

        reservationService.updateCurrentReservation(reservationId);
        apartmentService.updateApartmentsMatchingCurrentReservation();

        reservationService.getCurrentReservation().setApartmentId(apartmentId);

        model.addAttribute("isCompleted",false);
        model.addAttribute("reservation",reservationService.getCurrentReservation());
        model.addAttribute("matchingApartments",
                apartmentService.getApartmentsMatchingCurrentReservation());
        model.addAttribute("apartmentSelected",true);

        return "/reservation/reservation";
    }

    @PostMapping("/reservation/{id}/confirm")
    public String confirmCompletedReservation(@PathVariable("id") Integer reservationId){

        reservationService.updateCurrentReservation(reservationId);
        reservationService.getCurrentReservation().isCompleted();
        reservationService.updateReservationStatusAndConfirmationDateById(
                reservationId,
                ReservationStatus.CONFIRMED,
                LocalDate.now());

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/confirm/{apartmentId}")
    public String confirmReservation(@PathVariable("id") Integer reservationId,
                                     @PathVariable("apartmentId") Integer apartmentId){

        Optional<Apartment> optionalApartment = apartmentService.getApartmentById(apartmentId);
        optionalApartment.orElseThrow(IllegalStateException::new);

        Apartment apartment = optionalApartment.get();

        reservationService.updateCurrentReservation(reservationId);

        reservationService.updateReservationWithApartmentById(apartment,reservationId, LocalDate.now());
        apartmentService.updateApartmentStatusById(apartmentId, ApartmentStatus.RESERVED);

        if (!apartment.matchesReservation(reservationService.getCurrentReservation())){
            return "redirect:/error";
        }

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/cancel")
    public String dropReservation(@PathVariable("id") Integer reservationId){

        reservationService.updateCurrentReservation(reservationId);
        reservationService.updateReservationStatusById(reservationId, ReservationStatus.CANCELLED);

        if (reservationService.getCurrentReservation().getApartmentId()!=null) {
            apartmentService.updateApartmentStatusById(
                    reservationService.getCurrentReservation().getApartmentId(),
                    ApartmentStatus.AVAILABLE);
        }

        return "redirect:/";
    }

}
