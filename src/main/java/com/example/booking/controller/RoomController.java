package com.example.booking.controller;

import com.example.booking.entity.Room;
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
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;

    @Autowired
    public RoomController(RoomService roomService, RoomTypeService roomTypeService) {
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
    }
    @GetMapping("/list")
    public String showRoomList(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<Room> roomPage = roomService.showRoomList(pageable);
        model.addAttribute("rooms", roomPage.getContent());
        model.addAttribute("totalPages", roomPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "Rooms/listRoom";
    }
    @GetMapping("/searchRoom")
    public String getRoom(@RequestParam String roomNumber, Model model) {
        try {
            List<Room> rooms = roomService.searchRoomsByRoomNumber(roomNumber);
            if (rooms.isEmpty()) {
                model.addAttribute("errorMessage", "Room not found");
            } else {
                model.addAttribute("rooms", rooms);
            }
            return "Rooms/listRoom";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Room not found");
            return "errorPage";
        }
    }

    @GetMapping("/add")
    public String showAddFrom(@NotNull Model model){
       try {
           Room add = new Room();
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
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/add";
        }
        roomService.createRoom(room);
        return "redirect:/rooms/list";
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
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/add";
        }
        roomService.updateRoom(room);
        return "redirect:/rooms/list";
    }

}
