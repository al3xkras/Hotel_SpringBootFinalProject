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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private Optional<Reservation> currentReservation=Optional.empty();
    private Optional<List<Reservation>> currentUserActiveReservations=Optional.empty();
    private Optional<List<Reservation>> currentPendingReservations=Optional.empty();
    public void clearEverything(){
        currentReservation=Optional.empty();
        currentUserActiveReservations=Optional.empty();
        currentPendingReservations=Optional.empty();
    }

    private static final long daysToCancelPayment = 2L;

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
        reservationRepository.save(reservation);
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

    public void updateReservationStatusById(int id, ReservationStatus reservationStatus){
        reservationRepository.updateReservationStatusById(id, reservationStatus);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    public void updateReservationStatusAndConfirmationDateById(int id, ReservationStatus reservationStatus, LocalDate confirmationDate){
        reservationRepository.updateReservationStatusAndConfirmationDateById(id, reservationStatus, confirmationDate);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    public void updateReservationWithApartmentById(Apartment apartment, int id, LocalDate confirmationDate){
        reservationRepository.updateApartmentIdAndPriceAndReservationStatusAndConfirmationDateById(
                apartment.getId(),
                apartment.getPrice(),
                ReservationStatus.CONFIRMED,
                confirmationDate,
                id);

        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    public void updateReservationPaymentStatusById(int reservationId, boolean isPaid){
        reservationRepository.updateIsPaidById(reservationId,isPaid);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    public Reservation updateCurrentReservation(int reservationId){
        if (!currentReservation.isPresent() || currentReservation.get().getId()!=reservationId){
            currentReservation = getReservationById(reservationId);
        }
        updateReservationDaysUntilExpiration(getCurrentReservation());
        return getCurrentReservation();
    }

    public List<Reservation> updateCurrentUserActiveReservationsById(int userId){
        if (currentUserActiveReservations.isPresent() &&
                !currentUserActiveReservations.get().isEmpty() &&
                currentUserActiveReservations.get().get(0).getUserId()==userId){
            return getCurrentUserActiveReservations();
        }

        currentUserActiveReservations = Optional.of(getActiveReservationsByUserId(userId));
        currentUserActiveReservations.get().forEach(this::updateReservationDaysUntilExpiration);

        return getCurrentUserActiveReservations();
    }

    public List<Reservation> updateCurrentPendingReservations(){
        if (!currentPendingReservations.isPresent() || currentPendingReservations.get().isEmpty()) {
            currentPendingReservations = Optional.of(getPendingReservations());
        }
        return getCurrentPendingReservations();
    }

    private Reservation updateReservationDaysUntilExpiration(Reservation reservation){
        if (reservation.getAdminConfirmationDate()==null){
            return reservation;
        }
        LocalDate submitDate=reservation.getAdminConfirmationDate();
        long daysBetween = DAYS.between(LocalDate.now(),submitDate);
        reservation.setDaysUntilExpiration(daysToCancelPayment-daysBetween);
        return reservation;
    }

    public void clearCurrentUserActiveReservations(){currentUserActiveReservations=Optional.empty();}

    public void clearCurrentReservation(){
        currentReservation=Optional.empty();
    }

    public void clearCurrentPendingReservations(){
        currentPendingReservations=Optional.empty();
    }

    public Reservation getCurrentReservation() {
        return currentReservation.orElseThrow(IllegalStateException::new);
    }

    public List<Reservation> getCurrentUserActiveReservations() {
        return currentUserActiveReservations.orElseThrow(IllegalStateException::new);
    }

    public List<Reservation> getCurrentPendingReservations() {
        return currentPendingReservations.orElseThrow(IllegalStateException::new);
    }
}
