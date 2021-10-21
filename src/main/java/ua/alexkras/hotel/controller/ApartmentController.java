package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.model.UserType;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;
import java.time.LocalDateTime;
import java.util.Comparator;

@Controller
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final AuthController authController;
    private final ReservationService reservationService;

    @Autowired
    public ApartmentController(ApartmentService apartmentService,
                               ReservationService reservationService,
                               AuthController authController){
        this.apartmentService=apartmentService;
        this.authController=authController;
        this.reservationService=reservationService;
    }

    @GetMapping("/apartments")
    public String listApartments(@RequestParam(value = "sort",required=false) String by,Model model){

        apartmentService.updateApartments();

        by = by==null?"price":by;

        switch (by){
            case "price":
                apartmentService.getApartments().sort(Comparator.comparing(Apartment::getPrice));
                break;
            case "places":
                apartmentService.getApartments().sort(Comparator.comparing(Apartment::getPlaces).reversed());
                break;
            case "class":
                apartmentService.getApartments().sort(Comparator.comparing(Apartment::getApartmentClass));
                break;
            case "status":
                apartmentService.getApartments().sort(Comparator.comparing(Apartment::getStatus));
                break;
        }

        model.addAttribute("allApartments", apartmentService.getApartments());

        return "/apartment/apartments_menu";
    }

    @GetMapping("/add_apartment")
    public String apartmentAddPage(Model model){
        model.addAttribute("apartment",new Apartment());

        return "/apartment/add_apartment";
    }

    @PostMapping("/add_apartment")
    public String onApartmentAdd(
            @ModelAttribute("apartment") Apartment apartment){

        apartmentService.addApartment(apartment);

        return "redirect:/";
    }

    @GetMapping("/apartment/{id}")
    public String apartmentPage(@PathVariable("id") Integer id,
                                Model model){

        apartmentService.updateCurrentApartment(id);

        model.addAttribute("apartment",apartmentService.getCurrentApartment());
        model.addAttribute("reservation", new Reservation());

        return "/apartment/apartment";
    }

    @PostMapping("/apartment/{id}")
    public String submitExistingApartmentReservation(@PathVariable("id") Integer id,
                                                     @ModelAttribute("reservation") Reservation reservationDate,
                                                     Model model){

        if (reservationDate.getFromDate().compareTo(reservationDate.getToDate())>=0){
            apartmentService.updateCurrentApartment(id);
            model.addAttribute("apartment",apartmentService.getCurrentApartment());
            model.addAttribute("fromDateIsGreaterThanToDate",true);
            return "/apartment/apartment";
        }

        User currentUser = authController.getCurrentUser().orElseThrow(IllegalStateException::new);
        Apartment apartment = apartmentService.getApartmentById(id).orElseThrow(IllegalStateException::new);

        if (!apartment.getStatus().equals(ApartmentStatus.AVAILABLE) ||
                !currentUser.getUserType().equals(UserType.USER)){
            return "redirect:/apartments";
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(currentUser.getId());
        reservation.setApartmentClass(apartment.getApartmentClass());
        reservation.setFromDate(reservationDate.getFromDate());
        reservation.setToDate(reservationDate.getToDate());
        reservation.setSubmitDate(LocalDateTime.now());
        reservation.setPlaces(apartment.getPlaces());
        reservation.setApartmentId(apartment.getId());
        reservation.setApartmentPrice(apartment.getPrice());
        reservation.setReservationStatus(ReservationStatus.PENDING);

        apartmentService.updateApartmentStatusById(apartment.getId(),ApartmentStatus.RESERVED);
        reservationService.addReservation(reservation);
        return "redirect:/";
    }



}
