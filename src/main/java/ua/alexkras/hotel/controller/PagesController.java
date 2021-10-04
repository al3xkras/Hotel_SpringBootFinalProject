package ua.alexkras.hotel.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PagesController {

    @Value("${pageStrings.basename.path}")

    @RequestMapping("/")
    public String mainPage(){
        return "index";
    }

    

}
