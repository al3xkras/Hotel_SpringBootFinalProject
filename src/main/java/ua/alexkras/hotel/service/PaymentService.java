package ua.alexkras.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Payment;
import ua.alexkras.hotel.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository=paymentRepository;
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
}
