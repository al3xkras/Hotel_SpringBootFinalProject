package ua.alexkras.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.model.ApartmentStatus;

import java.util.Optional;

public interface ApartmentRepository extends JpaRepository<Apartment,Long> {
    Optional<Apartment> findApartmentById(Integer id);

    @Modifying(clearAutomatically = true)
    @Query("update Apartment apartment set apartment.status =:apartmentStatus where apartment.id =:id")
    void updateApartmentStatusById(@Param("id") int id, @Param("apartmentStatus") ApartmentStatus apartmentStatus);

}
