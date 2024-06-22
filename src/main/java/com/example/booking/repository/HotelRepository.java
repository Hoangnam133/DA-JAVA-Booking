package com.example.booking.repository;

import com.example.booking.entity.Hotel;
import com.example.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    Hotel findByUser(User user);
    Hotel findByHotelId(int hotelId);
}
