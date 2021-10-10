package ua.alexkras.hotel.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegistrationRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    private String username;

    @NotEmpty
    @Size(min=8, max = 25)
    private String password;

    @NotEmpty
    @Size(min=8, max = 25)
    private String passwordConfirm;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdayDate;

    @NotEmpty
    private String gender;

    @NotEmpty
    private String phoneNumber;
}
