package ua.alexkras.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

    List<Reservation> findAllByUserId(long userId);

    List<Reservation> findByUserIdAndIsActive(long userId, boolean isActive);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reservation reservation set reservation.isActive =:isActive where reservation.userId =:userId")
    void updateActiveByUserId(@Param("userId") long userId, @Param("isActive") boolean isActive);

    Optional<Reservation> findById(int reservationId);

    List<Reservation> findByActiveAndReservationStatus(boolean active,ReservationStatus reservationStatus);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reservation reservation set reservation.reservationStatus =:reservationStatus where reservation.id =:id")
    void updateReservationStatusById(@Param("id") long id, @Param("reservationStatus") ReservationStatus reservationStatus);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reservation reservation set reservation.reservationStatus =:reservationStatus, " +
            "reservation.adminConfirmationDate =:confirmationDate where reservation.id =:id")
    void updateReservationStatusAndConfirmationDateById(@Param("id") long id, @Param("reservationStatus") ReservationStatus reservationStatus,
                                                        @Param("confirmationDate") LocalDate confirmationDate);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reservation reservation set " +
            "reservation.apartmentId =:apartmentId, reservation.apartmentPrice =:apartmentPrice, " +
            "reservation.reservationStatus =:reservationStatus, " +
            "reservation.adminConfirmationDate =:confirmationDate  where reservation.id =:reservationId")
    void updateApartmentIdAndPriceAndReservationStatusAndConfirmationDateById(
            @Param("apartmentId") long apartmentId,
            @Param("apartmentPrice") int apartmentPrice,
            @Param("reservationStatus") ReservationStatus reservationStatus,
            @Param("confirmationDate") LocalDate confirmationDate,
            @Param("reservationId") long reservationId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Reservation reservation set reservation.isPaid =:isPaid where reservation.id =:id")
    void updateIsPaidById(@Param("id") long id, @Param("isPaid") boolean isPaid);
}
