package ua.alexkras.hotel.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import ua.alexkras.hotel.dto.RegistrationRequestDTO;
import ua.alexkras.hotel.entity.RegistrationRequest;
import ua.alexkras.hotel.service.RegistrationService;

@Controller
public class PagesController {


    private final LocaleResolver localeResolver;

    private final RegistrationService registrationService;

    @Autowired
    public PagesController(RegistrationService registrationService, LocaleResolver localeResolver){
        this.registrationService = registrationService;
        this.localeResolver=localeResolver;
    }

    @RequestMapping("/")
    public String mainPage(){
        return "index";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model){

        model.addAttribute("registrationRequest",new RegistrationRequestDTO());

        return "registration";
    }

    @PostMapping("/registration")
    public String sendRegistrationRequest(@ModelAttribute("registrationRequest") RegistrationRequestDTO dto){
        RegistrationRequest registrationRequest = new RegistrationRequest(dto,
                registrationService.getGlobalLoginRequestId());

        System.out.println(registrationRequest);


        return "index";
    }

}
