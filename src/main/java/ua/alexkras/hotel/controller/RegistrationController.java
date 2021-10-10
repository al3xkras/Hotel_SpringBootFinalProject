package ua.alexkras.hotel.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.dto.RegistrationRequest;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.UserType;

import javax.validation.Valid;
import java.sql.*;

@Slf4j
@Controller
public class RegistrationController {

    @GetMapping("/registration")
    public String registrationPage(Model model){
        model.addAttribute("registrationRequest", new RegistrationRequest());
        model.addAttribute("usernameExists",false);
        model.addAttribute("passwordMismatch",false);
        return "registration";
    }

    @PostMapping("/registration")
    public String sendRegistrationRequest(@Valid @ModelAttribute("registrationRequest") RegistrationRequest request, BindingResult result, Model model){
        if (result.hasErrors()){
            return "registration";
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())){
            model.addAttribute("passwordMismatch",true);
            return "registration";
        }

        User user;

        try {
            user = new User(request, UserType.USER);
        } catch (Exception e){
            return "registration";
        }

        try{
            UserDAO.addUser(user);
        } catch (SQLException e) {
            model.addAttribute("usernameExists",true);
            return "registration";
        }

        return "index";
    }

}
