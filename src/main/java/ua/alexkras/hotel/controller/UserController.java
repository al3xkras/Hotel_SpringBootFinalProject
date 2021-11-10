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
                reservationService.findAllByUserIdAndActive(optionalUser.get().getId(),true));

        return "personal_area/user";
    }


    @GetMapping("/reservation/{id}")
    public String reservationFromTable(@PathVariable("id") Integer reservationId,
                                       Model model){

        Reservation currentReservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        if (!currentReservation.isCompleted()){
            return "redirect:/";
        }

        model.addAttribute("reservation",currentReservation);
        model.addAttribute("isCompleted",true);
        model.addAttribute("userAccount",true);

        return "/reservation/reservation";
    }


    @PostMapping("/reservation/{id}/confirm")
    public String confirmReservation(@PathVariable("id") Integer reservationId){

        Reservation currentReservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        if (currentReservation.getUserId()!=
                authController.getCurrentUser().orElseThrow(IllegalStateException::new).getId() ||
                currentReservation.isExpired()){
            return "redirect:/";
        }

        reservationService.updateStatusById(reservationId,ReservationStatus.RESERVED);

        return "redirect:/";
    }


    @PostMapping("/reservation/{id}/cancel")
    public String cancelReservation(@PathVariable("id") Integer reservationId) {

        Reservation currentReservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        if (currentReservation.getUserId()!= authController
                .getCurrentUser().orElseThrow(IllegalStateException::new)
                .getId() ||
                currentReservation.isExpired()){
            return "redirect:/";
        }

        apartmentService.updateStatusById(
                currentReservation.getApartmentId(),
                ApartmentStatus.AVAILABLE);

        reservationService.updateStatusById(
                reservationId,
                ReservationStatus.CANCELLED);

        return "redirect:/";
    }


    @GetMapping("/reservation/{id}/make_payment")
    public String makePaymentPage(@PathVariable("id") Integer reservationId,
                                  Model model){
        User user = authController.getCurrentUser().orElseThrow(IllegalStateException::new);
        Reservation currentPaymentReservation = paymentService.updateCurrentPaymentReservationByReservationId(reservationId);

        Payment payment = new Payment();
        payment.setReservationId(reservationId);
        payment.setUserId(user.getId());
        payment.setValue(currentPaymentReservation.getApartmentPrice());

        model.addAttribute("payment",payment);

        return "/personal_area/user/payment";
    }


    @PostMapping("/reservation/{id}/make_payment")
    public String makePayment(@PathVariable("id") Integer reservationId,
                              @ModelAttribute("payment") Payment payment,
                              Model model){
        User user = authController.getCurrentUser().orElseThrow(IllegalStateException::new);

        Reservation currentPaymentReservation = paymentService.updateCurrentPaymentReservationByReservationId(reservationId);

        if (!payment.getCardCvv().matches("^(\\d{3})$")){
            payment.setReservationId(reservationId);
            payment.setUserId(user.getId());
            payment.setValue(currentPaymentReservation.getApartmentPrice());

            model.addAttribute("invalidCvv",true);
            return "/personal_area/user/payment";
        }

        Reservation reservation = reservationService.findById(reservationId)
                .orElseThrow(IllegalStateException::new);

        if (user.getId()!= reservation.getUserId()){
            return "redirect:/";
        }

        payment.setUserId(reservation.getUserId());
        payment.setReservationId(reservation.getId());
        payment.setValue(reservation.getApartmentPrice());
        payment.setPaymentDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        //TODO execute in transaction
        paymentService.addPayment(payment);
        reservationService.updateIsPaidById(reservationId,true);

        return "redirect:/";
    }


}
