package ua.alexkras.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.alexkras.hotel.entity.Apartment;

public interface ApartmentRepository extends JpaRepository<Apartment,Long> {

}
