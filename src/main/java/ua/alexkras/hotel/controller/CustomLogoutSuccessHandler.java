package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import ua.alexkras.hotel.model.HotelUserDetailsService;
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

    private final AuthController authController;
    private final ApartmentService apartmentService;
    private final PaymentService paymentService;
    private final ReservationService reservationService;


    @Autowired
    public CustomLogoutSuccessHandler(ApartmentService apartmentService,
                                      PaymentService paymentService,
                                      ReservationService reservationService,
                                      AuthController authController){
        this.apartmentService=apartmentService;
        this.paymentService=paymentService;
        this.reservationService=reservationService;
        this.authController=authController;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        HotelUserDetailsService.clearCurrentUserDetails();
        authController.clearCurrentUser();

        apartmentService.flush();
        reservationService.flush();
        paymentService.clearEverything();

        super.onLogoutSuccess(request, response, authentication);
    }
}
