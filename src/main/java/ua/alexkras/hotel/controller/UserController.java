package ua.alexkras.hotel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.alexkras.hotel.entity.Payment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.PaymentService;
import ua.alexkras.hotel.service.ReservationService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final AuthController authController;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ApartmentService apartmentService;

    @Autowired
    public UserController(AuthController authController,
                          ReservationService reservationService,
                          PaymentService paymentService,
                          ApartmentService apartmentService){
        this.authController=authController;
        this.reservationService=reservationService;
        this.paymentService=paymentService;
        this.apartmentService=apartmentService;
    }


    @GetMapping
    public String userMainPage(Model model){
        Optional<User> optionalUser = authController.getCurrentUser();

        if (!optionalUser.isPresent()){
            return "index";
        }

        model.addAttribute("allReservations",
                reservationService.getActiveReservationsByUserId(
                        optionalUser.get().getId()));

        return "personal_area/user";
    }


    @GetMapping("/reservation/{id}")
    public String reservationFromTable(@PathVariable("id") Integer reservationId,
                                       Model model){
        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !(optionalReservation=reservationService.getReservationById(reservationId)).isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (!reservation.isCompleted()){
            return "redirect:/";
        }

        model.addAttribute("reservation",reservation);

        model.addAttribute("isCompleted",true);
        model.addAttribute("userAccount",true);

        return "/reservation/reservation";
    }


    @PostMapping("/reservation/{id}/confirm")
    public String confirmReservation(@PathVariable("id") Integer reservationId){
        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !(optionalReservation=reservationService.getReservationById(reservationId)).isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (!authController.getCurrentUser().isPresent() ||
                reservation.getUserId()!=authController.getCurrentUser().get().getId()){
            return "redirect:/";
        }


        reservation.setReservationStatus(ReservationStatus.RESERVED);

        if (!reservationService.updateReservationStatusById(reservationId,ReservationStatus.RESERVED)){
            return "redirect:/error";
        }

        return "redirect:/";
    }


    @PostMapping("/reservation/{id}/cancel")
    public String cancelReservation(@PathVariable("id") Integer reservationId) {
        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !(optionalReservation=reservationService.getReservationById(reservationId)).isPresent()){
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (!authController.getCurrentUser().isPresent() ||
                reservation.getUserId()!=authController.getCurrentUser().get().getId()){
            return "redirect:/";
        }


        if (!apartmentService.updateApartmentStatusById(reservation.getApartmentId(), ApartmentStatus.AVAILABLE) ||
                !reservationService.updateReservationStatusById(reservationId,ReservationStatus.CANCELLED)){
            return "redirect:/error";
        }

        return "redirect:/";
    }


    @GetMapping("/reservation/{id}/make_payment")
    public String makePaymentPage(@PathVariable("id") Integer reservationId,
                                  Model model){
        if (reservationId==null || !authController.getCurrentUser().isPresent()){
            return "redirect:/error";
        }

        Payment payment = new Payment();

        payment.setReservationId(reservationId);

        model.addAttribute("payment",payment);

        return "/personal_area/user/payment";
    }


    @PostMapping("/reservation/{id}/make_payment")
    public String makePayment(@PathVariable("id") Integer reservationId,
                              @ModelAttribute("payment") Payment payment,
                              Model model){

        if (reservationId==null || !authController.getCurrentUser().isPresent()){
            return "redirect:/";
        }

        if (!payment.getCardCvv().matches("^(\\d{3})$")){
            payment.setReservationId(reservationId);
            model.addAttribute("invalidCvv",true);
            return "/personal_area/user/payment";
        }

        Optional<Reservation> optionalReservation = reservationService.getReservationById(reservationId);

        if (!optionalReservation.isPresent()) {
            return "redirect:/error";
        }

        Reservation reservation = optionalReservation.get();

        if (authController.getCurrentUser().get().getId()!=reservation.getUserId()){
            return "redirect:/";
        }

        payment.setUserId(reservation.getUserId());
        payment.setReservationId(reservation.getId());
        payment.setValue(reservation.getApartmentPrice());
        payment.setPaymentDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        if (!paymentService.addPayment(payment) ||
                !reservationService.updateReservationPaymentStatusById(reservationId,true)){
            return "redirect:/error";
        }

        return "redirect:/";
    }


}
