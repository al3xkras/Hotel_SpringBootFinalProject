package ua.alexkras.hotel.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.repository.ApartmentRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository){
        this.apartmentRepository=apartmentRepository;
    }

    public List<Apartment> getAllApartments(){
        return apartmentRepository.findAll();
    }

    public Optional<Apartment> getApartmentById(Integer id){
        return apartmentRepository.findApartmentById(id);
    }

}
