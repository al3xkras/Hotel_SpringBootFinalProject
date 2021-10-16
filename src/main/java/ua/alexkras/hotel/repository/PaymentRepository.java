package ua.alexkras.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.alexkras.hotel.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
