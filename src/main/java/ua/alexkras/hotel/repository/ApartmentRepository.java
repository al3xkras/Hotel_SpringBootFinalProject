package ua.alexkras.hotel.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ApartmentStatus;

import java.util.List;
import java.util.Optional;

public interface ApartmentRepository extends PagingAndSortingRepository<Apartment,Long> {
    Optional<Apartment> findById(long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Apartment apartment set apartment.status =:apartmentStatus where apartment.id =:id")
    void updateApartmentStatusById(@Param("id") long id, @Param("apartmentStatus") ApartmentStatus apartmentStatus);

    List<Apartment> findApartmentsByApartmentClassAndPlacesAndStatus(ApartmentClass apartmentClass,
                                                                     int places,
                                                                     ApartmentStatus apartmentStatus);

    Page<Apartment> findAll(Pageable pageable);
}
