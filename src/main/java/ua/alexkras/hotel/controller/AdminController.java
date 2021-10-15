package ua.alexkras.hotel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final ApartmentService apartmentService;

    private List<Apartment> matchingApartments;
    private Reservation currentReservation;

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
            return "redirect:/";
        }

        if (!updateCurrentReservation(id) || !updateMatchingApartments(currentReservation)){
            return "redirect:/error";
        }

        model.addAttribute("reservation",currentReservation);

        model.addAttribute("matchingApartments",
                matchingApartments);

        return "/reservation/reservation";
    }

    @GetMapping("/reservation/{id}/select/{apartmentId}")
    public String confirmReservationPage(@PathVariable("id") Integer reservationId,
                                         @PathVariable("apartmentId") Integer apartmentId,
                                         Model model){

        if (!updateCurrentReservation(reservationId) || !updateMatchingApartments(currentReservation)){
            return "redirect:/error";
        }

        currentReservation.setApartmentId(apartmentId);

        model.addAttribute("reservation",currentReservation);


        model.addAttribute("matchingApartments",
                matchingApartments);

        model.addAttribute("apartmentSelected",true);

        return "/reservation/reservation";
    }

    @PostMapping("/reservation/{id}/confirm/{apartmentId}")
    public String confirmReservation(@PathVariable("id") Integer reservationId,
                                     @PathVariable("apartmentId") Integer apartmentId){

        Optional<Apartment> optionalApartment = apartmentService.getApartmentById(apartmentId);

        if (reservationId==null || apartmentId==null || !optionalApartment.isPresent()){
            return "redirect:/error";
        }

        Apartment apartment = optionalApartment.get();

        updateCurrentReservation(reservationId);

        if (!apartment.matchesReservation(currentReservation)){
            return "redirect:/";
        }

        reservationService.updateReservationWithApartmentById(apartment,reservationId);

        currentReservation=null;

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/cancel")
    public String dropReservation(@PathVariable("id") Integer reservationId){

        if (reservationId==null || !reservationService
                .updateReservationStatusById(
                reservationId, ReservationStatus.CANCELLED)
            ){

            return "redirect:/error";
        }

        return "redirect:/";

    }

    private boolean updateCurrentReservation(int reservationId){
        if (currentReservation==null || currentReservation.getId()!=reservationId){
            currentReservation = reservationService.getReservationById(reservationId).orElse(null);

            return currentReservation != null;
        }
        return true;
    }

    private boolean updateMatchingApartments(@NotNull Reservation reservation){
        if (matchingApartments==null || !reservation.equals(currentReservation)){
            try {
                matchingApartments = apartmentService.findApartmentsMatchingReservation(reservation);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
