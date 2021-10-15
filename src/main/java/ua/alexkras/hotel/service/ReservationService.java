package ua.alexkras.hotel.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
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

    public Optional<Reservation> getReservationById(int reservationId){
        return reservationRepository.findById(reservationId);
    }

    public List<Reservation> getAllReservations(){
        return reservationRepository.findAll();
    }

    public List<Reservation> getPendingReservations(){
        return reservationRepository.findByReservationStatus(ReservationStatus.PENDING);
    }
}
