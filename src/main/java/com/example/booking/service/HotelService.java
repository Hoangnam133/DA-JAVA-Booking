package com.example.booking.service;

import com.example.booking.entity.Hotel;
import com.example.booking.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    @Autowired
    public HotelService(HotelRepository hotelRepository){
        this.hotelRepository = hotelRepository;
    }

    public void hotelCreate(Hotel hotel) {
        hotelRepository.save(hotel);
    }
    public List<Hotel> getAllHotels(){
        return hotelRepository.findAll();
    }
    public Hotel findHotelById(int id) {
        return hotelRepository.findByHotelId(id)
                .orElseThrow(()-> new IllegalArgumentException("Hotel not found"));
    }
    public void hotelUpdate(Hotel hotelUpdate) {
        try {
            Hotel existingHotel = hotelRepository.findById(hotelUpdate.getHotelId())
                            .orElseThrow(()-> new IllegalArgumentException("Hotel not found"));
            existingHotel.setHotelName(hotelUpdate.getHotelName());
            existingHotel.setHotelPhone(hotelUpdate.getHotelPhone());
            existingHotel.setHotelAddress(hotelUpdate.getHotelAddress());
            existingHotel.setHotelImage1(hotelUpdate.getHotelImage1());
            existingHotel.setHotelImage2(hotelUpdate.getHotelImage2());
            existingHotel.setHotelImage3(hotelUpdate.getHotelImage3());
            hotelRepository.save(existingHotel);
        }
        catch (Exception e) {
            throw  new RuntimeException(e.getMessage());
        }
    }
}
