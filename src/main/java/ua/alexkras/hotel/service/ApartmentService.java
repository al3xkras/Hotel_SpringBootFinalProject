package ua.alexkras.hotel.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.repository.ApartmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final ReservationService reservationService;

    private Apartment currentApartment;
    private List<Apartment> apartments;

    private List<Apartment> apartmentsMatchingCurrentReservation;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository,
                            ReservationService reservationService){
        this.apartmentRepository=apartmentRepository;
        this.reservationService=reservationService;
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
        clearApartments();
        clearCurrentApartment();
        clearApartmentsMatchingCurrentReservation();
        return true;
    }

    public boolean updateApartmentStatusById(int id,ApartmentStatus apartmentStatus){
        try {
            apartmentRepository.updateApartmentStatusById(id,apartmentStatus);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        clearApartments();
        clearCurrentApartment();
        clearApartmentsMatchingCurrentReservation();
        return true;
    }

    public List<Apartment> findApartmentsMatchingReservation(Reservation reservation){
        return apartmentRepository.findApartmentsByApartmentClassAndPlacesAndStatus(reservation.getApartmentClass(),
                reservation.getPlaces(), ApartmentStatus.AVAILABLE);
    }

    public boolean updateCurrentApartment(int apartmentId){
        if (currentApartment==null || currentApartment.getId()!=apartmentId){
            currentApartment = getApartmentById(apartmentId).orElse(null);

            return currentApartment != null;
        }
        return true;
    }

    public void clearCurrentApartment(){currentApartment=null;}

    public boolean updateApartments(){
        if (apartments==null){
            try {
                apartments = getAllApartments();
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean updateApartmentsMatchingCurrentReservation(){
        if (apartmentsMatchingCurrentReservation==null || apartmentsMatchingCurrentReservation.isEmpty()) {
            try {
                apartmentsMatchingCurrentReservation = findApartmentsMatchingReservation(reservationService.getCurrentReservation());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void clearApartmentsMatchingCurrentReservation(){
        apartmentsMatchingCurrentReservation=null;
    }

    public void clearApartments(){apartments=null;}

    public Apartment getCurrentApartment() {
        return currentApartment;
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public List<Apartment> getApartmentsMatchingCurrentReservation() {
        return apartmentsMatchingCurrentReservation;
    }
}
