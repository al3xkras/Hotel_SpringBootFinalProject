package ua.alexkras.hotel.entity;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ReservationStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, length = 32)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "apartment_id")
    private Long apartmentId;

    @Column(name = "apartment_class", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApartmentClass apartmentClass;

    @Column(name = "places",nullable = false)
    private int places;

    @Column(name = "price")
    private Integer apartmentPrice;

    @Column(name="reservation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "submit_date", nullable = false)
    private LocalDateTime submitDate;

    @Column(name = "confirmation_date")
    private LocalDate adminConfirmationDate;

    @Column(name = "id_paid", nullable = false)
    private boolean isPaid = false;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default 1")
    private boolean isActive = true;

    @Column(name = "is_expired", nullable = false, columnDefinition = "boolean default 0")
    private boolean expired = false;

    @Transient
    private Long daysUntilExpiration;

    public boolean isCompleted(){
        return apartmentId!=null & apartmentPrice!=null;
    }
}
