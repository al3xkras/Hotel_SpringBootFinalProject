package ua.alexkras.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.alexkras.hotel.entity.Apartment;

import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment,Long> {
    Optional<Apartment> findApartmentById(Integer id);
}
