package com.example.booking.service;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Room;
import com.example.booking.entity.User;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.RoomRepository;
import com.example.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }
    public Booking createBooking(Booking booking, Long userId, int roomId){
        Booking newBooking = new Booking();
        User existingUser = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        newBooking.setCheckInDate(booking.getCheckInDate());
        newBooking.setCheckOutDate(booking.getCheckOutDate());
        newBooking.setUser(existingUser);
        Room room = roomRepository.getRoomByRoomId(roomId);
        newBooking.setRoom(room);
        newBooking.setTotalPrice(totalPrice(booking));

        newBooking.setPaymentStatus(false);
        newBooking.setCancelStatus(false);
        newBooking.setBookingStatus(false);

        bookingRepository.save(booking);
        return newBooking;
    }
    private double totalPrice(Booking booking){
        double price = booking.getRoom().getPrice();
        long numberOfDays = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return price * numberOfDays;
    }
    public Booking cancelBooking (int bookingId){
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
        existingBooking.setCancelStatus(true);
        bookingRepository.save(existingBooking);
        return existingBooking;
    }
    public void checkIn(int bookingId){
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
        existingBooking.setBookingStatus(true);
        bookingRepository.save(existingBooking);
    }
    public void updatePaymentStatus(int bookingId){
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
        existingBooking.setPaymentStatus(true);
        bookingRepository.save(existingBooking);
    }
    public List<Booking> ShowBookingListOfGuest(Long userId){
        return bookingRepository.findByUser_IdAndBookingStatus(userId,false);
    }
    public List<Booking> ShowCancelBookingListOfGuest(Long userId){
        return bookingRepository.findAllByUser_IdAndCancelStatus(userId,true);
    }
    public List<Booking> showBookinListofAdmin(){
        return bookingRepository.findAll();
    }
    public Booking ShowBookingById(int bookingId){
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingId + " not found."));
    }


}
