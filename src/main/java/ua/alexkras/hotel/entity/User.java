package ua.alexkras.hotel.entity;

import lombok.*;
import ua.alexkras.hotel.dto.RegistrationRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {
    private String name;

    private String surname;

    private String username;

    private String password;

    private LocalDate birthday;

    private String gender;

    private String phoneNumber;

    private UserType userType;

    public User(RegistrationRequest dto, UserType userType){

        this.name=dto.getName();
        this.surname=dto.getSurname();
        this.username=dto.getUsername();
        this.password=dto.getPassword();
        try {
            this.birthday = MySqlStrings.dateFormat.parse(dto.getBirthdayDate()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException ex){
            this.birthday = null;
        }
        this.gender=dto.getGender();
        this.phoneNumber=dto.getPhoneNumber();

        this.userType=userType;
    }

    public User(String name, String surname, String username,
                String password, String phoneNumber, LocalDate birthday, String gender, UserType userType) {
        this.name=name;
        this.surname=surname;
        this.username=username;
        this.password=password;
        this.phoneNumber=phoneNumber;
        this.birthday=birthday;
        this.gender=gender;
        this.userType=userType;
    }

    public String toSqlString(){
        //FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, PHONE_NUMBER, BIRTHDAY, GENDER

        return String.format("\"%s\", \"%s\", \"%s\", \"%s\", \"%s\",  \"%s\", \"%s\", \"%s\"",
                name, surname, username, password, phoneNumber, birthday, gender, userType.name());
    }
}
