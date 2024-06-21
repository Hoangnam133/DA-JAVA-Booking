package com.example.booking.controller;

import com.example.booking.entity.Room;
import com.example.booking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
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
    public String availableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                 @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                 Model model) {
        List<Room> rooms = roomService.availableRooms(checkInDate, checkOutDate);
        model.addAttribute("rooms", rooms);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        return "searchRooms/availableRooms";
    }
}
