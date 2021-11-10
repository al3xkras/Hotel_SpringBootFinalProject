package ua.alexkras.hotel.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
public class Payment {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, length = 32)
    private long id;

    @Column(name = "user_id", nullable = false, length = 32)
    private long userId;

    @Column(name = "reservation_id", nullable = false, length = 32)
    private long reservationId;

    @Column(name = "value", nullable = false)
    private int value;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "card_expiration_date", nullable = false)
    private LocalDate cardExpirationDate;

    @Column(name = "card_cvv", nullable = false, length = 3)
    private String cardCvv;

}
