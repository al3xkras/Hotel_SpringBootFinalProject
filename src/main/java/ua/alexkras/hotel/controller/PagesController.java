package ua.alexkras.hotel.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PagesController {

    @RequestMapping("/")
    public String mainPage(){
        return "index";
    }

}
