package ua.alexkras.hotel.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ApartmentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Apartment {
    @Id
    @GenericGenerator(name="IdOrGenerated", strategy="ua.alexkras.hotel.model.UseIdOrGenerate")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return places == apartment.places && Objects.equals(id, apartment.id) && name.equals(apartment.name) && apartmentClass == apartment.apartmentClass && status == apartment.status && price.equals(apartment.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, places, apartmentClass, status, price);
    }
}
