package ua.alexkras.hotel.entity;

import lombok.*;
import ua.alexkras.hotel.dto.RegistrationRequestDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegistrationRequest {
    private long id;

    private String name;
    private String surname;
    private String username;
    private String password;
    private LocalDate birthday;
    private String gender;
    private String phoneNumber;
    private String locale;


    public RegistrationRequest(RegistrationRequestDTO dto, long id){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        this.id = id;
        this.name=dto.getName();
        this.surname=dto.getSurname();
        this.username=dto.getUsername();
        this.password=dto.getPassword();
        try {
            this.birthday = dateFormat.parse(dto.getBirthdayDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException ex){
            this.birthday = null;
        }
        this.gender=dto.getGender();
        this.phoneNumber=dto.getPhoneNumber();

        this.locale=dto.getLocale();

    }
}
