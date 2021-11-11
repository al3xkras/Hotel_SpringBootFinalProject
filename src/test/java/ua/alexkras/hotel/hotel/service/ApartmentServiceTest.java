package ua.alexkras.hotel.hotel.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import ua.alexkras.hotel.entity.Apartment;
import ua.alexkras.hotel.entity.Reservation;
import ua.alexkras.hotel.hotel.test_db_connection.TestDatabase;
import ua.alexkras.hotel.model.ApartmentClass;
import ua.alexkras.hotel.model.ApartmentStatus;
import ua.alexkras.hotel.service.ApartmentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ApartmentServiceTest {

    static Apartment testApartment4 = Apartment.builder()
            .id(4L)
            .name("test apartment 4")
            .places(2)
            .apartmentClass(ApartmentClass.ClassB)
            .status(ApartmentStatus.AVAILABLE)
            .price(6545)
            .build();


    private final ApartmentService apartmentService;
    private final TestDatabase testDatabase;

    @Autowired
    public ApartmentServiceTest(ApartmentService apartmentService, TestDatabase testDatabase) {
        this.apartmentService = apartmentService;
        this.testDatabase = testDatabase;
    }

    Reservation matchReservation1 = TestDatabase.matchReservation1;

    @BeforeTestExecution
    public void beforeTest(){
        testDatabase.createTestDatabase();
    }

    @Test
    public void testCreate() {
        apartmentService.create(testApartment4);
        Apartment actual = apartmentService.findById(testApartment4.getId())
                        .orElseThrow(IllegalStateException::new);

        assertEquals(testApartment4,actual);
    }

    @Test
    public void testFindById() {

        Apartment testApartment1 = TestDatabase.testApartment1;

        Apartment foundById = apartmentService
                .findById(testApartment1.getId())
                .orElseThrow(IllegalStateException::new);

        assertEquals(foundById,testApartment1);
    }

    @Test
    public void testFindAllApartments() {

        Pageable pageable1 = Pageable.ofSize(2);
        List<Apartment> found1 = apartmentService.findAll(pageable1).toList();

        assertEquals(2,found1.size());

        Pageable pageable2 = Pageable.ofSize(1);
        List<Apartment> found2 = apartmentService.findAll(pageable2).toList();

        assertEquals(1,found2.size());

        Pageable pageable3 = Pageable.ofSize(10);
        List<Apartment> found3 = apartmentService.findAll(pageable3).toList();

        assertEquals(3,found3.size());
    }

    @Test
    public void testFindApartmentsMatchingReservation() {
        Apartment testApartment2 = TestDatabase.testApartment2;

        Pageable pageable1 = Pageable.ofSize(10);

        List<Apartment> matching1 = apartmentService.findAllMatchingReservation(matchReservation1);

        assertEquals(1,matching1.size());
        assertEquals(matching1.get(0),testApartment2);
    }

    @Test
    public void testUpdateApartmentStatusById() {
        Apartment testApartment1 = TestDatabase.testApartment1;

        Apartment apartment1AfterUpdate = Apartment.builder()
                .id(testApartment1.getId())
                .name("test apartment 1")
                .places(3)
                .apartmentClass(ApartmentClass.ClassA)
                .status(ApartmentStatus.UNAVAILABLE)
                .price(1000)
                .build();

        apartmentService.updateStatusById(testApartment1.getId(),apartment1AfterUpdate.getStatus());

        Apartment afterUpdate = apartmentService
                .findById(testApartment1.getId())
                .orElseThrow(IllegalStateException::new);

        assertEquals(apartment1AfterUpdate,afterUpdate);
    }
}