package ua.alexkras.hotel.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ApartmentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@ToString
public class Apartment {
    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, length = 32)
    private Integer id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "PLACES", nullable = false)
    private int places;

    @Column(name = "APARTMENT_CLASS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApartmentClass apartmentClass;

    @Column(name = "APARTMENT_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApartmentStatus status;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    public boolean matchesReservation(@NotNull Reservation reservation){
        return places == reservation.getPlaces() & apartmentClass.equals(reservation.getApartmentClass()) & status.equals(ApartmentStatus.AVAILABLE);
    }
}
