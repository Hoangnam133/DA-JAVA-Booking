package com.example.booking.service;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Hotel;
import com.example.booking.entity.Room;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.HotelRepository;
import com.example.booking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
    }
    public List<Room> availableRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> availableRooms = roomRepository.findAllByRoomStatusIsTrue();
        List<Booking> bookingsInPeriod = bookingRepository.findAllByCancelStatusIsFalseAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(checkInDate, checkOutDate);
        for (Booking booking : bookingsInPeriod)
            availableRooms.remove(booking.getRoom());
        return availableRooms;
    }
    public void createRoom(Room room){
        List<Hotel> hotels = hotelRepository.findAll();
        Hotel hotel = hotels.getFirst();
        room.setHotel(hotel);
        roomRepository.save(room);
    }
    public void updateRoom(Room room){
        roomRepository.save(room);

    }
//    public List<Room> showRoomList(Pageable pageable){
//        return roomRepository.findAll();
//    }
    public Page<Room> showRoomList(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }
    public List<Room> activeRoomsList(){
        return roomRepository.findAllByRoomStatusIsTrue();
    }
    public Room searchRoom(int roomId){
        return roomRepository.findByRoomId(roomId);
    }
    public List<Room> searchRoomsByRoomNumber(String roomNumber){
        List<Room> findRoom = roomRepository.findAllByRoomNumber(roomNumber);
        if (findRoom == null){
            throw new RuntimeException("Room not found");
        }
        return findRoom;
    }



}
