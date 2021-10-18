package ua.alexkras.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Payment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationService reservationService;

    private Reservation currentPaymentReservation;
    public void clearEverything(){
        currentPaymentReservation=null;
    }

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          ReservationService reservationService){
        this.paymentRepository=paymentRepository;
        this.reservationService=reservationService;
    }

    public boolean addPayment(Payment payment){
        try{
            paymentRepository.save(payment);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean setCurrentPaymentReservationByReservationId(int reservationId) {
        if (currentPaymentReservation!=null && currentPaymentReservation.getId()==reservationId){
            return true;
        }

        try {
            currentPaymentReservation = reservationService.getReservationById(reservationId).orElseThrow(IllegalStateException::new);
        } catch (Exception e){
            return false;
        }

        return true;
    }

    public void clearCurrentPaymentReservation(){
        currentPaymentReservation=null;
    }

    public Reservation getCurrentPaymentReservation() {
        return currentPaymentReservation;
    }
}
