package com.example.booking.service;

import com.example.booking.entity.Booking;
import com.example.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;

    }
    public String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public void createBooking(Booking booking){
        booking.setCheckInStatus(false);
        booking.setCancelStatus(false);
        booking.setPaymentStatus(false);
        bookingRepository.save(booking);
    }

    public void cancelBooking (Booking booking){
        bookingRepository.save(booking);
    }

    public void checkIn(Booking booking){
        bookingRepository.save(booking);
    }
    public void updatePaymentStatus(int bookingId){
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
        existingBooking.setPaymentStatus(true);
        bookingRepository.save(existingBooking);
    }
    public void updateCheckInStatus(int bookingId){
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
        existingBooking.setCheckInStatus(true);
        bookingRepository.save(existingBooking);
    }
    public List<Booking> ShowBookingListOfUser(Long userId){
        List<Booking> bookings = bookingRepository.findByUser_IdOrderByBookingTimeDesc(userId);
        if (bookings == null){
            throw new RuntimeException("No bookings found");
        }
        return bookings;
    }
    public Page<Booking> showBookingListOfAdmin(Pageable pageable) {
        return bookingRepository.findAllByCheckInStatusFalseAndCancelStatusFalseAndPaymentStatusFalseOrderByCheckInDateDesc(pageable);
    }
    public Page<Booking> showBookingListCheckedOfAdmin(Pageable pageable){
        return bookingRepository.findAllByCheckInStatusTrueAndPaymentStatusFalseOrderByCheckInDateDesc(pageable);
    }
    public Booking findBookingById(int bookingId){
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
    public List<Booking> findBookingByPhone(String phone){
        return bookingRepository.findAllByCheckInStatusFalseAndUser_Phone(phone);
    }
    public List<Booking> findBookingConfirmByPhone(String phone){
        return bookingRepository.findAllByCheckInStatusTrueAndPaymentStatusFalseAndUser_Phone(phone);
    }
}
