package ua.alexkras.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.repository.ReservationRepository;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository=reservationRepository;
    }

    public void addReservation (Reservation reservation){
        try {
            reservationRepository.save(reservation);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public List<Reservation> getReservationsByUserId(int userId){
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getActiveReservationsByUserId(int userId){
        return reservationRepository.findByUserIdAndIsActive(userId,true);
    }

    public Optional<Reservation> getReservationById(int reservationId){
        return reservationRepository.findById(reservationId);
    }

    public List<Reservation> getAllReservations(){
        return reservationRepository.findAll();
    }

    public List<Reservation> getPendingReservations(){
        return reservationRepository.findByReservationStatus(ReservationStatus.PENDING);
    }

    public boolean updateReservationStatusById(int id, ReservationStatus reservationStatus){
        try {
            reservationRepository.updateReservationStatusById(id, reservationStatus);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateReservationWithApartmentById(Apartment apartment, int id){
        try {
            reservationRepository.updateApartmentIdAndPriceAndReservationStatusById(
                    apartment.getId(),
                    apartment.getPrice(),
                    ReservationStatus.CONFIRMED,
                    id);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
