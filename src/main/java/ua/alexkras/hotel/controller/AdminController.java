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
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.ReservationStatus;
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
            return "redirect:/";
        }

        if (!reservationService.updateCurrentReservation(id)){
            return "redirect:/error";
        }

        model.addAttribute("reservation",reservationService.getCurrentReservation());

        boolean isCompleted = reservationService.getCurrentReservation().isCompleted();
        model.addAttribute("isCompleted", isCompleted);

        if (!isCompleted) {
            model.addAttribute("matchingApartments",
                    apartmentService.findApartmentsMatchingReservation(reservationService.getCurrentReservation()));
        }

        return "/reservation/reservation";
    }

    @GetMapping("/reservation/{id}/select/{apartmentId}")
    public String confirmReservationPage(@PathVariable("id") Integer reservationId,
                                         @PathVariable("apartmentId") Integer apartmentId,
                                         Model model){

        if (!reservationService.updateCurrentReservation(reservationId)){
            return "redirect:/error";
        }

        reservationService.getCurrentReservation().setApartmentId(apartmentId);

        model.addAttribute("isCompleted",false);
        model.addAttribute("reservation",reservationService.getCurrentReservation());
        model.addAttribute("matchingApartments",
                apartmentService.findApartmentsMatchingReservation(reservationService.getCurrentReservation()));
        model.addAttribute("apartmentSelected",true);

        return "/reservation/reservation";
    }

    @PostMapping("/reservation/{id}/confirm")
    public String confirmCompletedReservation(@PathVariable("id") Integer reservationId){

        if (!reservationService.updateCurrentReservation(reservationId) ||
                !reservationService.getCurrentReservation().isCompleted() ||
                !reservationService.updateReservationStatusById(reservationId, ReservationStatus.CONFIRMED)){
            return "redirect:/error";
        }

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/confirm/{apartmentId}")
    public String confirmReservation(@PathVariable("id") Integer reservationId,
                                     @PathVariable("apartmentId") Integer apartmentId){

        Optional<Apartment> optionalApartment = apartmentService.getApartmentById(apartmentId);

        if (reservationId==null || apartmentId==null || !optionalApartment.isPresent()){
            return "redirect:/error";
        }

        Apartment apartment = optionalApartment.get();

        reservationService.updateCurrentReservation(reservationId);

        if (!apartment.matchesReservation(reservationService.getCurrentReservation())){
            return "redirect:/error";
        }

        reservationService.updateReservationWithApartmentById(apartment,reservationId);

        apartmentService.updateApartmentStatusById(apartmentId, ApartmentStatus.RESERVED);

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/cancel")
    public String dropReservation(@PathVariable("id") Integer reservationId){
        if (reservationId==null){
            return "redirect:/error";
        }

        Optional<Reservation> optionalReservation = reservationService.getReservationById(reservationId);

        if (!optionalReservation.isPresent() ||
                !reservationService.updateReservationStatusById(
                        reservationId, ReservationStatus.CANCELLED)){
            return "redirect:/error";
        }

        if (optionalReservation.get().getApartmentId()!=null) {
            apartmentService.updateApartmentStatusById(
                    optionalReservation.get().getApartmentId(),
                    ApartmentStatus.AVAILABLE);
        }

        return "redirect:/";
    }

}
