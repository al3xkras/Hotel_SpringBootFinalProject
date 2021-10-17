package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import ua.alexkras.hotel.dao.UserDAO;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.PaymentService;
import ua.alexkras.hotel.service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class CustomLogoutSuccessHandler extends
        SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ApartmentService apartmentService;
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    @Autowired
    public CustomLogoutSuccessHandler(ApartmentService apartmentService,
                                      PaymentService paymentService,
                                      ReservationService reservationService){
        this.apartmentService=apartmentService;
        this.paymentService=paymentService;
        this.reservationService=reservationService;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        UserDAO.setCurrentUser(null);

        apartmentService.clearApartments();
        apartmentService.clearCurrentApartment();

        reservationService.clearCurrentReservation();
        reservationService.clearCurrentUserActiveReservations();

        paymentService.clearCurrentPaymentReservation();

        super.onLogoutSuccess(request, response, authentication);
    }
}
