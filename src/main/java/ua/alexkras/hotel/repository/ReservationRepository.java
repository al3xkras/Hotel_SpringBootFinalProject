package ua.alexkras.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.model.ReservationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(int userId);

    Optional<Reservation> findById(int reservationId);

    List<Reservation> findByReservationStatus(ReservationStatus reservationStatus);
}
