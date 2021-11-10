package ua.alexkras.hotel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.alexkras.hotel.service.ReservationService;

@Slf4j
@Configuration
@EnableScheduling
public class ScheduledTasksConfig {

    private final ReservationService reservationService;

    @Autowired
    public ScheduledTasksConfig(ReservationService reservationService){
        this.reservationService=reservationService;
    }

    //@Scheduled(cron = "0 0 10 * * ?", zone = "Europe/London")
    @Scheduled(fixedDelay = 86400000)
    public void scheduleFindExpiredReservations(){
        reservationService.updateAllExpiredReservations();
        log.info("Expired Reservations has been updated");
    }
}
