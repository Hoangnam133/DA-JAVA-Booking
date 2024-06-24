package com.example.booking.repository;

import com.example.booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {


    List<Booking> findAllByUser_IdAndCancelStatus(Long userId, boolean cancelStatus);
    List<Booking> findByUser_IdAndCheckInStatus(Long userId, boolean bookingStatus);
    List<Booking> findAllByCancelStatusIsFalseAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(LocalDate checkInDate, LocalDate checkOutDate);
    List<Booking> findBookingByCheckInStatus(boolean checkInStatus);
//    List<Booking> findAllByCheckInStatusTrue();
    Page<Booking> findAllByCheckInStatusTrueOrderByCheckInDateDesc(Pageable pageable);
    Booking findBookingByPin(String pin);
//    Page<Booking> findAllByCheckInStatusFalseOrderByCheckInDateDesc(Pageable pageable);
    Page<Booking> findAllByCheckInStatusFalseAndCancelStatusFalseAndPaymentStatusFalseOrderByCheckInDateDesc(Pageable pageable);
    List<Booking> findAllByUser_Phone(String phone);
    List<Booking> findAllByCheckInStatusFalseAndUser_Phone(String phone);
    List<Booking> findAllByCheckInStatusTrueAndUser_Phone(String phone);
}
