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

        if (!getApartmentByIdIfNotPresent(id)){
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

        Optional<User> optionalUser = authController.getCurrentUser();


        if (!optionalUser.isPresent() | !getApartmentByIdIfNotPresent(id)){
            return "redirect:/error";
        }

        User currentUser = optionalUser.get();

        if (reservationDate.getFromDate().compareTo(reservationDate.getToDate())>=0){
            model.addAttribute("apartment",currentApartment);
            model.addAttribute("fromDateIsGreaterThanToDate",true);
            return "/apartment/apartment";
        }



        if (!currentApartment.getStatus().equals(ApartmentStatus.AVAILABLE)){
            return "redirect:/apartments";
        }

        Reservation reservation = new Reservation();

        reservation.setUserId(currentUser.getId());
        reservation.setApartmentClass(currentApartment.getApartmentClass());
        reservation.setFromDate(reservationDate.getFromDate());
        reservation.setToDate(reservationDate.getToDate());
        reservation.setSubmitDate(LocalDateTime.now());
        reservation.setPlaces(currentApartment.getPlaces());
        reservation.setApartmentId(currentApartment.getId());
        reservation.setApartmentPrice(currentApartment.getPrice());
        reservation.setReservationStatus(ReservationStatus.PENDING);

        currentApartment.setStatus(ApartmentStatus.RESERVED);

        apartmentService.updateApartmentStatus(currentApartment);
        currentApartment = null;

        reservationService.addReservation(reservation);

        System.out.println(reservation);

        return "redirect:/";
    }

    private boolean getApartmentByIdIfNotPresent(int id){
        if (currentApartment==null || currentApartment.getId()!=id){
            Optional<Apartment> optionalApartment = apartmentService.getApartmentById(id);

            if (!optionalApartment.isPresent())
                return false;

            currentApartment = optionalApartment.get();
        }
        return true;
    }
}
