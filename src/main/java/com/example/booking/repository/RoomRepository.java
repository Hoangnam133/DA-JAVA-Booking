package com.example.booking.repository;

import com.example.booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findAllByRoomStatusIsTrue();
    Room getRoomByRoomId(int roomId);
    List<Room> findAllByRoomStatusIsFalse();
    Room getRoomByRoomNumber(String roomNumber);
}
