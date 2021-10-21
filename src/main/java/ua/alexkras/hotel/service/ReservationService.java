package ua.alexkras.hotel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.model.MySqlStrings;
import ua.alexkras.hotel.model.ReservationStatus;
import ua.alexkras.hotel.repository.ReservationRepository;

import java.sql.*;
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

    private static final long daysToCancelPayment = 0L;

    private final LocalDate now = LocalDate.now();

    @Autowired
    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository=reservationRepository;
    }

    public boolean updateAllExpiredReservations(){
        try (Connection conn = DriverManager.getConnection(MySqlStrings.root, MySqlStrings.user, MySqlStrings.password);
             PreparedStatement updateExpired = conn.prepareStatement("UPDATE " +
                     "hotel_db.reservations SET " +
                     "status=?,"+
                     "expired=true "+
                     "WHERE not expired and not is_paid and " +
                     "admin_confirmation_date is not null and " +
                     "DATEDIFF(admin_confirmation_date,?)>=?");
             PreparedStatement setExpiredReservationApartmentsAvailable = conn.prepareStatement("UPDATE " +
                     "hotel_db.apartments SET "+
                     "apartment_status=? "+
                     "WHERE apartment_status='"+ApartmentStatus.RESERVED+"' and "+
                     "id IN (SELECT apartment_id FROM hotel_db.reservations WHERE expired and is_active)");
             PreparedStatement updateActive = conn.prepareStatement("UPDATE " +
                     "hotel_db.reservations SET " +
                     "is_active=false "+
                     "WHERE is_active and expired ")
             ){
            updateExpired.setString(1,ReservationStatus.CANCELLED.name());
            updateExpired.setDate(2, Date.valueOf(LocalDate.now()));
            updateExpired.setLong(3,daysToCancelPayment);
            updateExpired.executeUpdate();

            setExpiredReservationApartmentsAvailable.setString(1,ApartmentStatus.AVAILABLE.name());
            setExpiredReservationApartmentsAvailable.executeUpdate();

            updateActive.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
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
            currentReservation = getReservationById(reservationId).orElse(null);
        }
        if (currentReservation==null){
            return false;
        }
        updateReservationDaysUntilExpiration(currentReservation);
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

        currentUserActiveReservations.forEach(this::updateReservationDaysUntilExpiration);

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

    private void updateReservationDaysUntilExpiration(Reservation reservation){
        if (reservation.getAdminConfirmationDate()==null){
            return;
        }
        LocalDate submitDate=reservation.getAdminConfirmationDate();
        long daysBetween = DAYS.between(now,submitDate);
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
