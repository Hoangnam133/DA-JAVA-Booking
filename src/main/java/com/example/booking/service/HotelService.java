package com.example.booking.service;

import com.example.booking.entity.Hotel;
import com.example.booking.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final String uploadDir; // Thư mục lưu trữ ảnh

    @Autowired
    public HotelService(HotelRepository hotelRepository, @Value("${file.upload-dir}") String uploadDir) {
        this.hotelRepository = hotelRepository;
        this.uploadDir = uploadDir;
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
            existingHotel.setHotelStatus(hotelUpdate.isHotelStatus());
            hotelRepository.save(existingHotel);
        }
        catch (Exception e) {
            throw  new RuntimeException(e.getMessage());
        }
    }

}
