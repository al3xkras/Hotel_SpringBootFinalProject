package ua.alexkras.hotel.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private Page<Apartment> apartments;
    public void clearEverything(){
        currentApartment=null;
        apartments=null;
    }

    private Integer currentReservationId;
    private List<Apartment> apartmentsMatchingCurrentReservation;

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository,
                            ReservationService reservationService){
        this.apartmentRepository=apartmentRepository;
        this.reservationService=reservationService;
    }

    public Page<Apartment> getAllApartments(Pageable pageable){
        return apartmentRepository.findAll(pageable);
    }

    public Optional<Apartment> getApartmentById(Integer id){
        return apartmentRepository.findApartmentById(id);
    }

    public void addApartment(Apartment apartment) {
        apartmentRepository.save(apartment);
        clearApartments();
        clearCurrentApartment();
        clearApartmentsMatchingCurrentReservation();
    }

    public void updateApartmentStatusById(int id,ApartmentStatus apartmentStatus) {
        apartmentRepository.updateApartmentStatusById(id,apartmentStatus);
        clearApartments();
        clearCurrentApartment();
        clearApartmentsMatchingCurrentReservation();
    }

    public List<Apartment> findApartmentsMatchingReservation(Reservation reservation){
        return apartmentRepository.findApartmentsByApartmentClassAndPlacesAndStatus(reservation.getApartmentClass(),
                reservation.getPlaces(), ApartmentStatus.AVAILABLE);
    }

    public void updateCurrentApartment(int apartmentId) {
        if (currentApartment==null || currentApartment.getId()!=apartmentId){
            currentApartment = getApartmentById(apartmentId).orElseThrow(IllegalStateException::new);
        }
    }

    public void clearCurrentApartment(){currentApartment=null;}

    public void updateApartmentsMatchingCurrentReservation(){
        if (apartmentsMatchingCurrentReservation==null || apartmentsMatchingCurrentReservation.isEmpty() ||
                currentReservationId==null || currentReservationId!=reservationService.getCurrentReservation().getId()) {
            apartmentsMatchingCurrentReservation = findApartmentsMatchingReservation(reservationService.getCurrentReservation());
            currentReservationId=reservationService.getCurrentReservation().getId();
        }
    }

    public void clearApartmentsMatchingCurrentReservation(){
        apartmentsMatchingCurrentReservation=null;
    }

    public void clearApartments(){apartments=null;}

    public Apartment getCurrentApartment() {
        return currentApartment;
    }

    public Page<Apartment> getApartments() {
        return apartments;
    }

    public List<Apartment> getApartmentsMatchingCurrentReservation() {
        return apartmentsMatchingCurrentReservation;
    }
}
