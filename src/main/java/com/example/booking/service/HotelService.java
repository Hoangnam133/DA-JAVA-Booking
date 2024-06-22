package com.example.booking.service;

import com.example.booking.entity.Hotel;
import com.example.booking.entity.User;
import com.example.booking.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;
    private UserService userService;

    public Hotel hotelCreate(Hotel hotel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();

            // Check if the user already has a hotel
            Hotel existingHotel = hotelRepository.findByUser(currentUser);
            if (existingHotel != null) {
                throw new IllegalStateException("User already has a hotel created.");
            }

            Hotel newHotel = new Hotel();
            newHotel.setHotelAddress(hotel.getHotelAddress());
            newHotel.setHotelName(hotel.getHotelName());
            newHotel.setHotelImage1(hotel.getHotelImage1());
            newHotel.setHotelImage2(hotel.getHotelImage2());
            newHotel.setHotelImage3(hotel.getHotelImage3());
            newHotel.setUser(currentUser);

            return hotelRepository.save(newHotel);
        } else {
            throw new IllegalStateException("No authenticated user found or user is not an instance of User.");
        }
    }
    public List<Hotel> getAllHotels(){
        return hotelRepository.findAll();
    }
    public Hotel findHotelById(int id) {
        return hotelRepository.findByHotelId(id);
    }
    public Hotel hotelUpdate(Hotel hotelUpdate) {
        try {
            Hotel existingHotel = hotelRepository.findById(hotelUpdate.getHotelId()).get();
            existingHotel.setHotelName(hotelUpdate.getHotelName());
            existingHotel.setHotelPhone(hotelUpdate.getHotelPhone());
            existingHotel.setHotelAddress(hotelUpdate.getHotelAddress());
            existingHotel.setHotelImage1(hotelUpdate.getHotelImage1());
            existingHotel.setHotelImage2(hotelUpdate.getHotelImage2());
            existingHotel.setHotelImage3(hotelUpdate.getHotelImage3());
            return hotelRepository.save(existingHotel);
        }
        catch (Exception e) {
            throw  new RuntimeException(e.getMessage());
        }

    }
}
