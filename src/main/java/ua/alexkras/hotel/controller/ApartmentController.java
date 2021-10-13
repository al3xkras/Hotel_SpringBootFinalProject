package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.service.ApartmentService;

import java.util.Optional;

@Controller
public class ApartmentController {

    private final ApartmentService apartmentService;

    @Autowired
    public ApartmentController(ApartmentService apartmentService){
        this.apartmentService=apartmentService;
    }

    @GetMapping("/apartments")
    public String listApartments(Model model){

        model.addAttribute("allApartments",
                apartmentService.getAllApartments());

        return "/apartment/apartments_menu";
    }

    @GetMapping("/apartment/{id}")
    public String apartmentPage(@PathVariable("id") Integer id,
                                Model model){
        if (id==null){
            return "redirect:/apartments";
        }

        Optional<Apartment> optionalApartment = apartmentService.getApartmentById(id);

        if (!optionalApartment.isPresent()){
            return "redirect:/apartments";
        }

        model.addAttribute("apartment",optionalApartment.get());

        return "/apartment/apartment";
    }

    @GetMapping("/add_apartment")
    public String apartmentAddPage(){
        return "/apartment/add_apartment";
    }

    @PostMapping("/add_apartment")
    public String onApartmentAdd(){
        return null;
    }
}
