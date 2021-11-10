package ua.alexkras.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import ua.alexkras.hotel.entity.User;
import ua.alexkras.hotel.model.HotelUserDetailsService;
import ua.alexkras.hotel.repository.UserRepository;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }

    /**
     * Get user from a data source by username (login)
     *
     * @param username User's login (username)
     * @return Optional\<User>, if user was found, Optional.empty() otherwise.
     */
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    /**
     * Add new user to a data source
     * -Encode user's password, using HotelUserDetailsService.passwordEncoder(),
     *   before saving it to the data source
     *
     * @param user User to add
     * @throws RuntimeException if User was not added to the data source
     */
    public void create(User user){
        String notEncodedPassword=user.getPassword();
        boolean userWasAdded = false;
        try {
            user.setPassword(HotelUserDetailsService.passwordEncoder().encode(user.getPassword()));
            userRepository.save(user);
            userWasAdded=true;
        } catch (Exception e){
            user.setPassword(notEncodedPassword);
        }
        if (!userWasAdded){
            throw new RuntimeException("Error adding new "+user.toString());
        }
    }


}
