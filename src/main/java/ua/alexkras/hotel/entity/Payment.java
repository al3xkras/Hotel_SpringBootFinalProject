package ua.alexkras.hotel.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;



@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
public class Payment {
    @Id
    @GenericGenerator(name="IdOrGenerated", strategy="ua.alexkras.hotel.model.UseIdOrGenerate")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id == payment.id && userId == payment.userId && reservationId == payment.reservationId && value == payment.value && paymentDate.equals(payment.paymentDate) && cardNumber.equals(payment.cardNumber) && cardExpirationDate.equals(payment.cardExpirationDate) && cardCvv.equals(payment.cardCvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, reservationId, value, paymentDate, cardNumber, cardExpirationDate, cardCvv);
    }
}
