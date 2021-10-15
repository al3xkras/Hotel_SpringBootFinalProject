package ua.alexkras.hotel.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.repository.ApartmentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    public boolean addApartment(Apartment apartment){
        try {
            apartmentRepository.save(apartment);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateApartmentStatusById(int id,ApartmentStatus apartmentStatus){
        try {
            apartmentRepository.updateApartmentStatusById(id,apartmentStatus);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Apartment> findApartmentsMatchingReservation(Reservation reservation){
        return apartmentRepository.findApartmentsByApartmentClassAndPlacesAndStatus(reservation.getApartmentClass(),
                reservation.getPlaces(), ApartmentStatus.AVAILABLE);
    }

}
