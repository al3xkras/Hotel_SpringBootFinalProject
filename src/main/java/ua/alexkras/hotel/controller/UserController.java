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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

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

        if ( !reservationService.updateCurrentUserActiveReservationsById(optionalUser.get().getId()) ){
            return "redirect:/error";
        }

        model.addAttribute("allReservations", reservationService.getCurrentUserActiveReservations());

        return "personal_area/user";
    }


    @GetMapping("/reservation/{id}")
    public String reservationFromTable(@PathVariable("id") Integer reservationId,
                                       Model model){

        if (reservationId==null || !reservationService.updateCurrentReservation(reservationId)){
            return "redirect:/error";
        }

        if (!reservationService.getCurrentReservation().isCompleted()){
            return "redirect:/";
        }

        model.addAttribute("reservation",reservationService.getCurrentReservation());

        model.addAttribute("isCompleted",true);
        model.addAttribute("userAccount",true);

        return "/reservation/reservation";
    }


    @PostMapping("/reservation/{id}/confirm")
    public String confirmReservation(@PathVariable("id") Integer reservationId){

        if (reservationId==null ||
                !reservationService.updateCurrentReservation(reservationId)){
            return "redirect:/error";
        }


        if (!authController.getCurrentUser().isPresent() ||
                reservationService.getCurrentReservation().getUserId()!=authController.getCurrentUser().get().getId() ||
                reservationService.getCurrentReservation().isExpired()){
            return "redirect:/";
        }


        reservationService.getCurrentReservation().setReservationStatus(ReservationStatus.RESERVED);

        if (!reservationService.updateReservationStatusById(reservationId,ReservationStatus.RESERVED)){
            return "redirect:/error";
        }

        return "redirect:/";
    }


    @PostMapping("/reservation/{id}/cancel")
    public String cancelReservation(@PathVariable("id") Integer reservationId) {
        Optional<Reservation> optionalReservation;

        if (reservationId==null ||
                !reservationService.updateCurrentReservation(reservationId)){
            return "redirect:/error";
        }

        ;

        if (!authController.getCurrentUser().isPresent() ||
                reservationService.getCurrentReservation().getUserId()!=
                        authController.getCurrentUser().get().getId() ||
                reservationService.getCurrentReservation().isExpired()){
            return "redirect:/";
        }


        if (
                !apartmentService.updateApartmentStatusById(
                        reservationService.getCurrentReservation().getApartmentId(),
                        ApartmentStatus.AVAILABLE
                ) ||
                !reservationService.updateReservationStatusById(
                        reservationId,
                        ReservationStatus.CANCELLED
                )
        ){
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

        if (!paymentService.setCurrentPaymentReservationByReservationId(reservationId)){
            return "redirect:/";
        }

        Payment payment = new Payment();

        payment.setReservationId(reservationId);
        payment.setUserId(authController.getCurrentUser().get().getId());
        payment.setValue(paymentService.getCurrentPaymentReservation().getApartmentPrice());

        model.addAttribute("payment",payment);

        return "/personal_area/user/payment";
    }


    @PostMapping("/reservation/{id}/make_payment")
    public String makePayment(@PathVariable("id") Integer reservationId,
                              @ModelAttribute("payment") Payment payment,
                              Model model){

        if (reservationId==null || !authController.getCurrentUser().isPresent() ||
                !paymentService.setCurrentPaymentReservationByReservationId(reservationId)){
            return "redirect:/";
        }

        if (!payment.getCardCvv().matches("^(\\d{3})$")){
            payment.setReservationId(reservationId);
            payment.setUserId(authController.getCurrentUser().get().getId());
            payment.setValue(paymentService.getCurrentPaymentReservation().getApartmentPrice());

            model.addAttribute("invalidCvv",true);
            return "/personal_area/user/payment";
        }



        if (!reservationService.updateCurrentReservation(reservationId)) {
            return "redirect:/error";
        }



        if (authController.getCurrentUser().get().getId()!=
                reservationService.getCurrentReservation().getUserId()){
            return "redirect:/";
        }

        payment.setUserId(reservationService
                .getCurrentReservation().getUserId());
        payment.setReservationId(reservationService
                .getCurrentReservation().getId());
        payment.setValue(reservationService
                .getCurrentReservation().getApartmentPrice());
        payment.setPaymentDate(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        if (!paymentService.addPayment(payment) ||
                !reservationService.updateReservationPaymentStatusById(reservationId,true)){
            return "redirect:/error";
        }

        return "redirect:/";
    }


}
