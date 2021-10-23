package ua.alexkras.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Payment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.repository.PaymentRepository;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;

    private Optional<Reservation> currentPaymentReservation = Optional.empty();
    public void clearEverything(){
        clearCurrentPaymentReservation();
    }

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          ReservationService reservationService){
        this.paymentRepository=paymentRepository;
        this.reservationService=reservationService;
    }

    public void addPayment(Payment payment){
        paymentRepository.save(payment);
    }

    public Reservation updateCurrentPaymentReservationByReservationId(int reservationId) {
        if (currentPaymentReservation.isPresent() && currentPaymentReservation.get().getId()==reservationId){
            return getCurrentPaymentReservation();
        }
        currentPaymentReservation = reservationService.getReservationById(reservationId);
        return getCurrentPaymentReservation();
    }

    public void clearCurrentPaymentReservation(){
        currentPaymentReservation=Optional.empty();
    }

    public Reservation getCurrentPaymentReservation() {
        return currentPaymentReservation.orElseThrow(IllegalStateException::new);
    }
}
