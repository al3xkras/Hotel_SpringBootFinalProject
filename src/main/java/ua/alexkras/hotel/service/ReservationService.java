package ua.alexkras.hotel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private Reservation currentReservation;
    private List<Reservation> currentUserActiveReservations;
    private List<Reservation> currentPendingReservations;
    public void clearEverything(){
        currentReservation=null;
        currentUserActiveReservations=null;
        currentPendingReservations=null;
    }

    private static final long daysToCancelPayment = 2L;

    private final LocalDate now = LocalDate.now();

    @Autowired
    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository=reservationRepository;
    }

    public void updateAllExpiredReservations(){

    }

    public void addReservation (Reservation reservation){
        try {
            reservationRepository.save(reservation);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        clearCurrentReservation();
        clearCurrentUserActiveReservations();
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
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
        return true;
    }

    public boolean updateReservationStatusAndConfirmationDateById(int id, ReservationStatus reservationStatus, LocalDate confirmationDate){
        try {
            reservationRepository.updateReservationStatusAndConfirmationDateById(id, reservationStatus, confirmationDate);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
        return true;
    }

    public boolean updateReservationWithApartmentById(Apartment apartment, int id, LocalDate confirmationDate){
        try {
            reservationRepository.updateApartmentIdAndPriceAndReservationStatusAndConfirmationDateById(
                    apartment.getId(),
                    apartment.getPrice(),
                    ReservationStatus.CONFIRMED,
                    confirmationDate,
                    id);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
        return true;
    }

    public boolean updateReservationPaymentStatusById(int reservationId, boolean isPaid){
        try{
            reservationRepository.updateIsPaidById(reservationId,isPaid);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
        return true;
    }

    public boolean updateCurrentReservation(int reservationId){
        if (currentReservation==null || currentReservation.getId()!=reservationId){
            log.info("updating current reservation...");
            currentReservation = getReservationById(reservationId).orElse(null);
        }
        if (currentReservation==null){
            return false;
        }
        updateReservationExpiredStatus(currentReservation);
        return true;
    }

    public boolean updateCurrentUserActiveReservationsById(int userId){
        if (currentUserActiveReservations==null || currentUserActiveReservations.isEmpty() ||
                currentUserActiveReservations.get(0).getUserId()!=userId){
            try {
                currentUserActiveReservations = getActiveReservationsByUserId(userId);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        currentUserActiveReservations.forEach(this::updateReservationExpiredStatus);

        return true;
    }

    public boolean updateCurrentPendingReservations(){
        if (currentPendingReservations==null || currentPendingReservations.isEmpty()) {
            try {
                currentPendingReservations = getPendingReservations();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void updateReservationExpiredStatus(Reservation reservation){
        if (reservation.getAdminConfirmationDate()==null){
            reservation.setExpired(false);
            return;
        }

        LocalDate submitDate=reservation.getAdminConfirmationDate();
        long daysBetween = DAYS.between(now,submitDate);

        reservation.setExpired(daysBetween<0 || daysBetween>=daysToCancelPayment);

        if (reservation.getReservationStatus().equals(ReservationStatus.PENDING)){
            reservation.setExpired(false);
        }
        reservation.setDaysUntilExpiration(daysToCancelPayment-daysBetween);
    }

    public void clearCurrentUserActiveReservations(){currentUserActiveReservations=null;}

    public void clearCurrentReservation(){
        currentReservation=null;
    }

    public void clearCurrentPendingReservations(){
        currentPendingReservations=null;
    }

    public Reservation getCurrentReservation() {
        return currentReservation;
    }

    public List<Reservation> getCurrentUserActiveReservations() {
        return currentUserActiveReservations;
    }

    public List<Reservation> getCurrentPendingReservations() {
        return currentPendingReservations;
    }
}
