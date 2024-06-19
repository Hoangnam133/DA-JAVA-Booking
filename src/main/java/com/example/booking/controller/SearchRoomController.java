package com.example.booking.controller;

import com.example.booking.entity.Room;
import com.example.booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("searchRooms")
public class SearchRoomController {
    private final RoomService roomService;

    @Autowired
    public SearchRoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    @GetMapping("/availableRooms")
    public String availableRooms(@RequestParam("checkInDate") String checkInDate,
                                 @RequestParam("checkOutDate") String checkOutDate,
                                 Model model) {
        LocalDate startDate = LocalDate.parse(checkInDate);
        LocalDate endDate = LocalDate.parse(checkOutDate);
        List<Room> availableRooms = roomService.availableRooms(startDate, endDate);
        model.addAttribute("availableRooms", availableRooms);
        return "Rooms/availableRooms";
    }
}
