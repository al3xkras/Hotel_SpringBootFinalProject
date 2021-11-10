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
@ToString
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, length = 32)
    private long id;

    @Column(name = "USER_ID", nullable = false)
    private long userId;

    @Column(name = "APARTMENT_CLASS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApartmentClass apartmentClass;


    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "FROM_DATE", nullable = false)
    private LocalDateTime fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "TO_DATE", nullable = false)
    private LocalDateTime toDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "SUBMIT_DATE", nullable = false)
    private LocalDateTime submitDate;

    @Column(name = "ADMIN_CONFIRMATION_DATE")
    private LocalDate adminConfirmationDate;

    @Column(name = "PLACES",nullable = false)
    private int places;

    @Column(name = "APARTMENT_ID")
    private Long apartmentId;

    @Column(name = "APARTMENT_PRICE")
    private Integer apartmentPrice;

    @Column(name = "IS_PAID", nullable = false)
    private boolean isPaid = false;

    @Column(name="STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(name = "IS_ACTIVE", nullable = false, columnDefinition = "boolean default 1")
    private boolean isActive = true;

    @Column(name = "EXPIRED", nullable = false, columnDefinition = "boolean default 0")
    private boolean expired = false;

    @Transient
    private Long daysUntilExpiration;

    public boolean isCompleted(){
        return apartmentId!=null & apartmentPrice!=null;
    }
}
