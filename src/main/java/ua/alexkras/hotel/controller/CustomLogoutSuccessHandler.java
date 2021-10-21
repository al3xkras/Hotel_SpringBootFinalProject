package ua.alexkras.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import ua.alexkras.hotel.model.CustomUserDetailsService;
import ua.alexkras.hotel.service.ApartmentService;
import ua.alexkras.hotel.service.PaymentService;
import ua.alexkras.hotel.service.ReservationService;
import ua.alexkras.hotel.service.UserService;

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
    private final UserService userService;


    @Autowired
    public CustomLogoutSuccessHandler(ApartmentService apartmentService,
                                      PaymentService paymentService,
                                      ReservationService reservationService,
                                      UserService userService){
        this.apartmentService=apartmentService;
        this.paymentService=paymentService;
        this.reservationService=reservationService;
        this.userService=userService;
    }

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        CustomUserDetailsService.clearCurrentUser();

        apartmentService.clearEverything();
        reservationService.clearEverything();
        paymentService.clearEverything();

        super.onLogoutSuccess(request, response, authentication);
    }
}
