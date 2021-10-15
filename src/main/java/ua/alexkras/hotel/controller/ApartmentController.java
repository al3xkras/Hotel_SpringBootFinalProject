package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.model.UserType;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final AuthController authController;
    private final ReservationService reservationService;

    private Apartment currentApartment;

    @Autowired
    public ApartmentController(ApartmentService apartmentService,
                               ReservationService reservationService,
                               AuthController authController){
        this.apartmentService=apartmentService;
        this.authController=authController;
        this.reservationService=reservationService;
    }

    @GetMapping("/apartments")
    public String listApartments(Model model){

        model.addAttribute("allApartments",
                apartmentService.getAllApartments());

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
        if (id==null){
            return "redirect:/apartments";
        }

        if (!updateCurrentApartment(id)){
            return "redirect:/apartments";
        }

        model.addAttribute("apartment",currentApartment);
        model.addAttribute("reservation", new Reservation());

        return "/apartment/apartment";
    }

    @PostMapping("/apartment/{id}")
    public String submitExistingApartmentReservation(@PathVariable("id") Integer id,
                                                     @ModelAttribute("reservation") Reservation reservationDate,
                                                     Model model){

        if (reservationDate.getFromDate().compareTo(reservationDate.getToDate())>=0){
            if (!updateCurrentApartment(id))
                return "redirect:/error";
            model.addAttribute("apartment",currentApartment);
            model.addAttribute("fromDateIsGreaterThanToDate",true);
            return "/apartment/apartment";
        }

        Optional<User> optionalUser = authController.getCurrentUser();
        Optional<Apartment> optionalApartment = apartmentService.getApartmentById(id);

        if (!optionalUser.isPresent() | !optionalApartment.isPresent()){
            return "redirect:/error";
        }

        User currentUser = optionalUser.get();
        Apartment apartment = optionalApartment.get();

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

        apartment.setStatus(ApartmentStatus.RESERVED);

        apartmentService.updateApartmentStatus(apartment);

        reservationService.addReservation(reservation);

        currentApartment=null;

        System.out.println(reservation);

        return "redirect:/";
    }

    private boolean updateCurrentApartment(int apartmentId){
        if (currentApartment==null || currentApartment.getId()!=apartmentId){
            currentApartment = apartmentService.getApartmentById(apartmentId).orElse(null);

            return currentApartment != null;
        }
        return true;
    }

}
