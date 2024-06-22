package com.example.booking.repository;

import com.example.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> findAllByUser_IdAndCancelStatus(Long userId, boolean cancelStatus);
    List<Booking> findByUser_IdAndBookingStatus(Long userId, boolean bookingStatus);
    // Example of corrected method
    List<Booking> findAllByCancelStatusIsFalseAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(LocalDate checkInDate, LocalDate checkOutDate);
}
