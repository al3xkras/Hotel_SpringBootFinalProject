package ua.alexkras.hotel.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegistrationRequestDTO {
    private String name;
    private String surname;
    private String username;
    private String password;
    private String passwordConfirm;
    private String birthdayDate;
    private String gender;
    private String phoneNumber;
    private String locale;
}
