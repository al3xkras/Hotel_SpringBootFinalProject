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
import java.time.Duration;
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
    private List<Reservation> currentUserActiveReservations=new ArrayList<>();
    private List<Reservation> currentPendingReservations=new ArrayList<>();

    public void flush(){
        clearCurrentUserActiveReservations();
        clearCurrentReservation();
        clearCurrentPendingReservations();
    }

    private static final long daysToCancelPayment = 2L;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository=reservationRepository;
    }

    public void updateAllExpiredReservations(){
        try (Connection conn = DriverManager.getConnection(MySqlStrings.root, MySqlStrings.user, MySqlStrings.password);
             PreparedStatement updateExpired = conn.prepareStatement(MySqlStrings.updateExpired);
             PreparedStatement setExpiredReservationApartmentsAvailable = conn.prepareStatement(MySqlStrings.setExpiredReservationApartmentsAvailable);
             PreparedStatement updateActive = conn.prepareStatement(MySqlStrings.updateActive)
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
            throw new RuntimeException();
        }
    }

    public void create(Reservation reservation){
        reservationRepository.save(reservation);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
    }

    public List<Reservation> findAllByActiveAndStatus(boolean isActive, ReservationStatus reservationStatus){
        if (currentPendingReservations.isEmpty()) {
            currentPendingReservations = reservationRepository.findAllByIsActiveAndReservationStatus(isActive,reservationStatus);
        }
        return currentPendingReservations;
    }

    /**
     * Get full cost of a @reservation
     * Full cost is calculated by formula:
     *  (reservation's apartment price for 1 day)*(date difference in days between reservation's "from date" and "to date")
     * @param reservation valid reservation ("from date","to date",all apartment-related columns are not-null)
     * @throws NullPointerException if @reservation is invalid
     * @return full cost of @reservation
     */
    public int getReservationFullCost(Reservation reservation){
        return reservation.getApartmentPrice() *
                (int) Duration.between(reservation.getFromDate(),
                        reservation.getToDate()).toDays();
    }

    /**
     * Update Reservation status by id
     *
     * @param id id of Reservation to be updated
     * @param reservationStatus reservation status to assign to the Reservation
     */
    public void updateStatusById(long id, ReservationStatus reservationStatus){
        reservationRepository.updateReservationStatusById(id, reservationStatus);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    /**
     * Update Reservation status and date of confirmation by admin by Reservation's id
     *
     * @param id id of Reservation to be updated
     * @param reservationStatus new Reservation's status (after admins confirmation)
     * @param confirmationDate date of confirmation by Admin
     */
    public void updateStatusAndConfirmationDateById(long id, ReservationStatus reservationStatus, LocalDate confirmationDate){
        reservationRepository.updateReservationStatusAndConfirmationDateById(id, reservationStatus, confirmationDate);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    /**
     * Update Reservation's associated apartment (apartment id, price), and
     * set status of reservation to 'Confirmed', and set confirmation date
     * to @confirmationDate
     *
     * @param id id of Reservation
     * @param apartment apartment to associate with Reservation
     * @param confirmationDate date of confirmation by Admin
     */
    public void updateReservationApartmentDataAndConfirmationDateByIdWithApartment(long id, Apartment apartment, LocalDate confirmationDate){
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

    /**
     * Update Reservation's payment status by id
     * @param reservationId id of Reservation
     * @param isPaid new payment status
     */
    public void updateIsPaidById(long reservationId, boolean isPaid){
        reservationRepository.updateIsPaidById(reservationId,isPaid);
        clearCurrentReservation();
        clearCurrentUserActiveReservations();
        clearCurrentPendingReservations();
    }

    /**
     * Update current Reservation by reservation id
     * -If current reservation is not initialized, or
     *   current reservation's id is not equal to @reservationId:
     *
     *   -request new Reservation by @reservationId from data source
     *   -updates current reservation's days until expiration (@Transient field)
     * -Otherwise:
     *
     *   -Only updates current reservation's days until expiration (@Transient field)
     *
     * @param reservationId id of Reservation
     * @return newly updated (or existing) Reservation
     * @throws IllegalStateException if Reservation with @reservationId was not found in a data source
     */
    public Optional<Reservation> findById(long reservationId){
        if (!currentReservation.isPresent() || currentReservation.get().getId()!=reservationId){
            currentReservation = reservationRepository.findById(reservationId);
        }
        currentReservation.ifPresent(this::updateReservationDaysUntilExpiration);
        return currentReservation;
    }

    /**
     * Update current User's active reservations by User id
     * -If List of current User's active reservations are present, and
     *   the list is not empty, and
     *   the list contains reservations with userId equal to @userId
     *   (only first item of the list is checked, assuming all the items have the same userId):
     *
     *   -Does not request new List of Reservations from a data source
     * -Otherwise:
     *
     *   -Request new List of Reservations from a data source
     *   -Updates every reservation's days until expiration (@Transient field) in the list
     *
     * @param userId id of User to find Reservations by
     * @return newly created, or existing List of Reservations,
     *   which are active and created by user with id @userId
     */
    public List<Reservation> findAllByUserIdAndActive(long userId, boolean isActive){
        if (!currentUserActiveReservations.isEmpty() && currentUserActiveReservations.get(0).getUserId()==userId){
            return currentUserActiveReservations;
        }
        currentUserActiveReservations = reservationRepository.findAllByUserIdAndIsActive(userId,isActive);
        currentUserActiveReservations.forEach(this::updateReservationDaysUntilExpiration);
        return currentUserActiveReservations;
    }



    /**
     * Update reservation's days until expiration
     * -If reservation is not confirmed by admin:
     *   -Do nothing
     *
     * -Otherwise:
     *   -Calculate days between confirmation date and today
     *   -Update Reservation's days until expiration
     * @param reservation Reservation that will be updated
     */
    private void updateReservationDaysUntilExpiration(Reservation reservation){
        if (reservation.getAdminConfirmationDate()==null){
            return;
        }
        LocalDate submitDate=reservation.getAdminConfirmationDate();
        long daysBetween = DAYS.between(LocalDate.now(),submitDate);
        reservation.setDaysUntilExpiration(daysToCancelPayment-daysBetween);
    }

    private void clearCurrentUserActiveReservations(){currentUserActiveReservations=new ArrayList<>();}

    private void clearCurrentReservation(){
        currentReservation=Optional.empty();
    }

    private void clearCurrentPendingReservations(){
        currentPendingReservations=new ArrayList<>();
    }
}
