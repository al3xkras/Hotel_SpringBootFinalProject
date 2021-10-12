package ua.alexkras.hotel.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.repository.ApartmentRepository;

@Slf4j
@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository){
        this.apartmentRepository=apartmentRepository;
    }

}
