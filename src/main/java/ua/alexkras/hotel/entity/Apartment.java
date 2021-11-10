package ua.alexkras.hotel.entity;


import lombok.*;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ApartmentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
public class Apartment {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, length = 32)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "places", nullable = false)
    private int places;

    @Column(name = "apartment_class", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApartmentClass apartmentClass;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApartmentStatus status;

    @Column(name = "price", nullable = false)
    private Integer price;

    public boolean matchesReservation(@NotNull Reservation reservation){
        return places == reservation.getPlaces() & apartmentClass.equals(reservation.getApartmentClass()) & status.equals(ApartmentStatus.AVAILABLE);
    }
}
