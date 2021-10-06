package ua.alexkras.hotel.dto;

import lombok.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


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

    @NotEmpty
    private String birthdayDate;

    private String gender;

    private String phoneNumber;

    private String locale;
}
