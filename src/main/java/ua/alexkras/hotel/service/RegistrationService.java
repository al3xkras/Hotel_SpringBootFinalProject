package ua.alexkras.hotel.service;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private long id = -1L;

    @Bean
    public long getGlobalLoginRequestId(){
        id++;
        return id;
    }

}
