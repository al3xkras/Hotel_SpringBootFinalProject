package ua.alexkras.hotel.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import ua.alexkras.hotel.dto.RegistrationRequest;
import ua.alexkras.hotel.model.UserType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    @Id
    @GenericGenerator(name="IdOrGenerated", strategy="ua.alexkras.hotel.model.UseIdOrGenerate")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "surname", nullable = false, length = 30)
    private String surname;

    @Column(name = "username", nullable = false, length = 15)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", length = 30, nullable = false)
    private String phoneNumber;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "gender", length = 10, nullable = false)
    private String gender;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public User(RegistrationRequest dto, UserType userType){

        this.name=dto.getName();
        this.surname=dto.getSurname();
        this.username=dto.getUsername();
        this.password=dto.getPassword();

        this.birthday=dto.getBirthdayDate();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && name.equals(user.name) && surname.equals(user.surname) && username.equals(user.username) && password.equals(user.password) && phoneNumber.equals(user.phoneNumber) && birthday.equals(user.birthday) && gender.equals(user.gender) && userType == user.userType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, username, password, phoneNumber, birthday, gender, userType);
    }
}
