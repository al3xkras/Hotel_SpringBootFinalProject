package ua.alexkras.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.repository.ApartmentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    private Optional<Apartment> currentApartment=Optional.empty();
    public void flush(){
        clearCurrentApartment();
    }

    private Optional<Long> currentReservationId;
    private List<Apartment> apartmentsMatchingReservation = new ArrayList<>();

    @Autowired
    public ApartmentService(ApartmentRepository apartmentRepository,
                            ReservationService reservationService){
        this.apartmentRepository=apartmentRepository;
    }

    public Page<Apartment> findAll(Pageable pageable){
        return apartmentRepository.findAll(pageable);
    }

    public Optional<Apartment> findById(long id){
        if (!currentApartment.isPresent() || currentApartment.get().getId()!=id){
            currentApartment = apartmentRepository.findById(id);
        }
        return currentApartment;
    }

    public void create(Apartment apartment) {
        apartmentRepository.save(apartment);
        clearCurrentApartment();
        clearApartmentsMatchingCurrentReservation();
    }

    /**
     * Update Apartment's status by apartment id
     *
     * @param id Apartment's id
     * @param apartmentStatus New apartment status
     */
    public void updateStatusById(long id, ApartmentStatus apartmentStatus) {
        apartmentRepository.updateApartmentStatusById(id,apartmentStatus);
        clearCurrentApartment();
        clearApartmentsMatchingCurrentReservation();
    }

    /**
     * Update List of Apartments, that match @reservation
     * -If List of apartments is empty,
     *   or apartments in list do not match current Reservation:
     *   Requests List of apartments from a data source
     * -Otherwise:
     *   returns previously saved in memory list of apartments, that match reservation
     * @param reservation reservation to find matching apartments for
     * @return List of apartments, that match @reservation
     */
    public List<Apartment> findAllMatchingReservation(Reservation reservation){
        if (apartmentsMatchingReservation.isEmpty() ||
                !currentReservationId.isPresent() ||
                currentReservationId.get()!=reservation.getId()) {

            apartmentsMatchingReservation = apartmentRepository
                    .findApartmentsByApartmentClassAndPlacesAndStatus(
                        reservation.getApartmentClass(),
                        reservation.getPlaces(),
                        ApartmentStatus.AVAILABLE);

            currentReservationId=Optional.of(reservation.getId());
        }
        return apartmentsMatchingReservation;
    }

    private void clearCurrentApartment(){
        currentApartment=Optional.empty();
    }

    private void clearApartmentsMatchingCurrentReservation(){
        apartmentsMatchingReservation = new ArrayList<>();
    }
}
