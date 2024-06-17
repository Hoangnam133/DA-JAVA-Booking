package com.example.booking.service;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Hotel;
import com.example.booking.entity.Room;
import com.example.booking.repository.BookingRepository;
import com.example.booking.repository.HotelRepository;
import com.example.booking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Room createRoom(Room room){
        return roomRepository.save(room);
    }
    public Room updateRoom(Room room){
        Room existingRoom = searchRoom(room.getRoomId());
        existingRoom.setRoomNumber(room.getRoomNumber());
        existingRoom.setRoomImage1(room.getRoomImage1());
        existingRoom.setRoomImage2(room.getRoomImage2());
        existingRoom.setPrice(room.getPrice());
        existingRoom.setDescription(room.getDescription());
        roomRepository.save(existingRoom);
        return room;
    }
    public List<Room> showRoomList(){
        return roomRepository.findAll();
    }
    public List<Room> activeRoomsList(){
        return roomRepository.findAllByRoomStatusIsTrue();
    }
    public Room searchRoom(int roomId){
        return roomRepository.findById(roomId)
                .orElseThrow(()-> new RuntimeException("Room not found"));
    }
    public Room searchRoomsByRoomNumber(String roomNumber){
        Room findRoom =  roomRepository.getRoomByRoomNumber(roomNumber);
        if (findRoom == null){
            throw new RuntimeException("Room not found");
        }
        return findRoom;
    }



}
