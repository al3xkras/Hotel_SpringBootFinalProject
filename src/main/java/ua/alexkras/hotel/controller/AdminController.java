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
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.ReservationService;
import java.time.LocalDate;
import java.util.List;

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
        List<Reservation> pending = reservationService.findAllByActiveAndStatus(true,ReservationStatus.PENDING);
        model.addAttribute("pendingReservations", pending);
        return "personal_area/admin";
    }

    @GetMapping("/reservation/{id}")
    public String pendingReservationPage(@PathVariable Integer id, Model model){
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(IllegalStateException::new);

        model.addAttribute("reservation",reservation);

        boolean isCompleted = reservation.isCompleted();
        model.addAttribute("isCompleted", isCompleted);

        if (!isCompleted) {
            List<Apartment> matching = apartmentService.findAllMatchingReservation(reservation);
            model.addAttribute("matchingApartments", matching);
        }

        return "/reservation/reservation";
    }

    @GetMapping("/reservation/{id}/select/{apartmentId}")
    public String confirmReservationPage(@PathVariable("id") Integer reservationId,
                                         @PathVariable("apartmentId") Integer apartmentId,
                                         Model model){

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);
        reservation.setApartmentId(apartmentId);

        model.addAttribute("isCompleted",false);
        model.addAttribute("reservation",reservation);
        model.addAttribute("matchingApartments",
                apartmentService.findAllMatchingReservation(reservation));
        model.addAttribute("apartmentSelected",true);

        return "/reservation/reservation";
    }

    @PostMapping("/reservation/{id}/confirm")
    public String confirmCompletedReservation(@PathVariable("id") Integer reservationId){

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        if (!reservation.isCompleted())
            return "redirect:/error";
        reservationService.updateStatusAndConfirmationDateById(
                reservationId,
                ReservationStatus.CONFIRMED,
                LocalDate.now());

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/confirm/{apartmentId}")
    public String confirmReservation(@PathVariable("id") Integer reservationId,
                                     @PathVariable("apartmentId") Integer apartmentId){

        Apartment apartment = apartmentService.findById(apartmentId)
                .orElseThrow(IllegalStateException::new);

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        if (!apartment.matchesReservation(reservation)){
            return "redirect:/error";
        }

        //TODO execute in transaction
        reservationService.updateReservationApartmentDataAndConfirmationDateByIdWithApartment(
                reservationId, apartment, LocalDate.now());
        apartmentService.updateStatusById(apartmentId, ApartmentStatus.RESERVED);

        return "redirect:/";
    }

    @PostMapping("/reservation/{id}/cancel")
    public String dropReservation(@PathVariable("id") Integer reservationId){

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        //TODO execute in transaction
        if (reservation.getApartmentId()!=null) {
            apartmentService.updateStatusById(
                    reservation.getApartmentId(),
                    ApartmentStatus.AVAILABLE);
        }

        reservationService.updateStatusById(reservationId, ReservationStatus.CANCELLED);

        return "redirect:/";
    }

}
