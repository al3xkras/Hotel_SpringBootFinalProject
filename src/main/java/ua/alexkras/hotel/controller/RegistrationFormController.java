package ua.alexkras.hotel.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.dto.RegistrationRequest;
import ua.alexkras.hotel.entity.MySqlStrings;
import ua.alexkras.hotel.entity.User;

import javax.validation.Valid;
import java.sql.*;

@Slf4j
@Controller
public class RegistrationFormController {

    //private final MySQLConnectionService mySQLConnection;

    @Autowired
    public RegistrationFormController(){

    }

    @GetMapping("/registration")
    public String registrationPage(Model model){
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "registration";
    }

    @PostMapping("/registration")
    public String sendRegistrationRequest(@Valid RegistrationRequest request, BindingResult result, Model model){
        if (result.hasErrors()){
            return "registration";
        }

        User user;
        try {
            user = new User(request);
        } catch (Exception e){
            return "registration";
        }

        try (Connection conn = DriverManager.getConnection(MySqlStrings.connectionUrl, MySqlStrings.user, MySqlStrings.password);
             PreparedStatement addUserIfNotExists = conn.prepareStatement(
                     String.format(MySqlStrings.sqlInsertIntoUserDB, user.toSqlString()))
             ){

            addUserIfNotExists.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(user);
        return "index";
    }

}
