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
    public static String generateRandomString() {
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
        booking.setPin(generateRandomString());
        System.out.println(booking.getTotalPrice());
        bookingRepository.save(booking);
    }

    public void cancelBooking (Booking booking, String reasonCancel){
        booking.setCancelStatus(true);
        booking.setCancellationReason(reasonCancel);
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
    public List<Booking> ShowBookingListOfUser(Long userId){
        List<Booking> bookings = bookingRepository.findByUser_IdAndCheckInStatus(userId,false);
        if (bookings == null){
            throw new RuntimeException("No bookings found");
        }
        return bookings;
    }
    public List<Booking> ShowCancelBookingListOfUser(Long userId){
        List<Booking> bookingsIsCanceled = bookingRepository.findAllByUser_IdAndCancelStatus(userId,true);
        if (bookingsIsCanceled == null){
            throw new RuntimeException("No bookings found");
        }
        return bookingsIsCanceled;
    }
//    public List<Booking> showBookingListOfAdmin(){
//        List<Booking> bookings =bookingRepository.findAllByCheckInStatus(false);
//        if (bookings == null){
//            throw new RuntimeException("No bookings found");
//        }
//        return bookings;
//    }
public Page<Booking> showBookingListOfAdmin(Pageable pageable) {
    return bookingRepository.findAllByCheckInStatusFalseAndCancelStatusFalseAndPaymentStatusFalseOrderByCheckInDateDesc(pageable);
}
//    public List<Booking> showBookingListCheckedOfAdmin(){
//        List<Booking> bookingsChecked =  bookingRepository.findAllByCheckInStatusTrue();
//        if (bookingsChecked == null){
//            throw new RuntimeException("No bookings checked found");
//        }
//        return bookingsChecked;
//    }
public Page<Booking> showBookingListCheckedOfAdmin(Pageable pageable){
    return bookingRepository.findAllByCheckInStatusTrueOrderByCheckInDateDesc(pageable);
}
    public Booking findBookingById(int bookingId){
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
    public List<Booking> findBookingByPhone(String phone){
        return bookingRepository.findAllByCheckInStatusFalseAndUser_Phone(phone);
    }
    public List<Booking> findBookingConfirmByPhone(String phone){
        return bookingRepository.findAllByCheckInStatusFalseAndUser_Phone(phone);
    }

    public Booking searchBookingByPin(String pin){
        return bookingRepository.findBookingByPin(pin);
    }
}
