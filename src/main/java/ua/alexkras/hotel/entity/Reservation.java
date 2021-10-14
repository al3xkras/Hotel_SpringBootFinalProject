package ua.alexkras.hotel.entity;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ReservationStatus;

import javax.persistence.*;
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
    private Integer id;

    @Column(name = "USER_ID", nullable = false)
    private Integer userId;


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

    @Column(name = "PLACES",nullable = false)
    private Integer places;

    @Column(name = "APARTMENT_ID")
    private Integer apartmentId;

    @Column(name = "APARTMENT_PRICE")
    private Integer apartmentPrice;

    @Column(name = "IS_PAID", nullable = false)
    private boolean isPaid = false;

    @Column(name="STATUS",nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

}
