package com.example.booking.controller;

import com.example.booking.entity.Room;
import com.example.booking.service.HotelService;
import com.example.booking.service.RoomService;
import com.example.booking.service.RoomTypeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;

    @Autowired
    public RoomController(RoomService roomService, HotelService hotelService, RoomTypeService roomTypeService) {
        this.roomService = roomService;
        this.hotelService = hotelService;
        this.roomTypeService = roomTypeService;
    }
    @GetMapping("/List")
    public String showRoomList(Model model) {
        List<Room> rooms = roomService.showRoomList();
        model.addAttribute("rooms", rooms);
        return "Rooms/list";
    }
    @GetMapping("/searchRoom/{roomNumber}")
    public String getRoom(String roomNumber,Model model){
        try {
            Room findRoom = roomService.searchRoomsByRoomNumber(roomNumber);
            List<Room> rooms = new ArrayList<>();
            rooms.add(findRoom);
            model.addAttribute("room",rooms);
            return "Rooms/list";
        }catch (RuntimeException e){
            model.addAttribute("errorMessage", "Room not found");
            return "errorPage";
        }
    }
    @GetMapping("/add/{hotelId}")
    public String showAddFrom(@PathVariable int hotelId,@NotNull Model model){
       try {
           Room add = new Room();
           add.setHotel(hotelService.findHotelById(hotelId));
           model.addAttribute("room",add);
           model.addAttribute("roomType", roomTypeService.getAll());
           return "Rooms/add";
       }catch (Exception ex){
           throw new IllegalArgumentException("add fail");
       }
    }
    @PostMapping("/save")
    public String saveAddFrom(@Valid @ModelAttribute("room") Room room, @NotNull BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "Rooms/add";
        }
        roomService.createRoom(room);
        return "redirect:/List";
    }
    @GetMapping("/edit/{roomId}")
    public String showEditFrom(@PathVariable int roomId, Model model){
        try {
            Room existingRoom = roomService.searchRoom(roomId);
            model.addAttribute("room", existingRoom);
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/edit";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", "Room not found");
            return "errorPage";
        }
    }
    @PostMapping("/saveEdit/{roomId}")
    public String saveEditForm(@PathVariable int roomId, @Valid Room room, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            room.setRoomId(roomId);
            return "Rooms/add";
        }
        roomService.updateRoom(room);
        return "redirect:/List";
    }

}
