package ua.alexkras.hotel.entity;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ua.alexkras.hotel.model.ApartmentClass;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Reservation {

    private int userId;

    @NotEmpty
    private ApartmentClass apartmentClass;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fromDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime toDate;

    @NotEmpty
    private Integer places;
}
