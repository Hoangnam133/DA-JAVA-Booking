package com.example.booking.controller;

import com.example.booking.entity.Room;
import com.example.booking.service.HandleImageService;
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

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    @Autowired
    private HandleImageService handleImageService;
    @Autowired
    public RoomController(RoomService roomService, RoomTypeService roomTypeService) {
        this.roomService = roomService;
        this.roomTypeService = roomTypeService;
    }
    @GetMapping("/list")
    public String showRoomList(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 4);
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
    public String saveAddForm(@Valid @ModelAttribute("room") Room room,
                              @RequestParam("roomImage1File") MultipartFile roomImage1File,
                              @RequestParam("roomImage2File") MultipartFile roomImage2File,
                              @NotNull BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/add";
        }

        try {
            if (!roomImage1File.isEmpty()) {
                String image1Path = handleImageService.saveImage(roomImage1File);
                room.setRoomImage1(image1Path);
            }

            if (!roomImage2File.isEmpty()) {
                String image2Path = handleImageService.saveImage(roomImage2File);
                room.setRoomImage2(image2Path);
            }

            roomService.createRoom(room);
            return "redirect:/rooms/list";
        } catch (IOException ex) {
            model.addAttribute("errors", new String[]{"Lỗi lưu ảnh phòng: " + ex.getMessage()});
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/add";
        }
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
    public String saveEditForm(@PathVariable int roomId, @Valid @ModelAttribute("room") Room room,
                               BindingResult result, @RequestParam(value = "roomImage1File",required = false) MultipartFile roomImage1File,
                               @RequestParam(value = "roomImage2File",required = false) MultipartFile roomImage2File, Model model) {
        if (result.hasErrors()) {
            var errors = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            room.setRoomId(roomId);
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/edit";
        }

        try {
            // Handle room image 1
            if (roomImage1File != null && !roomImage1File.isEmpty()) {
                String roomImage1Path = handleImageService.saveImage(roomImage1File);
                room.setRoomImage1(roomImage1Path);
            } else {
                // Retain current image if no new image is selected
                Room existingRoom = roomService.searchRoom(roomId);
                room.setRoomImage1(existingRoom.getRoomImage1());
            }

            // Handle room image 2
            if (roomImage2File != null && !roomImage2File.isEmpty()) {
                String roomImage2Path = handleImageService.saveImage(roomImage2File);
                room.setRoomImage2(roomImage2Path);
            } else {
                // Retain current image if no new image is selected
                Room existingRoom = roomService.searchRoom(roomId);
                room.setRoomImage2(existingRoom.getRoomImage2());
            }
        } catch (IOException e) {
            model.addAttribute("errors", new String[]{"Image upload failed: " + e.getMessage()});
            room.setRoomId(roomId);
            model.addAttribute("roomType", roomTypeService.getAll());
            return "Rooms/edit";
        }

        roomService.updateRoom(room);
        return "redirect:/rooms/list";
    }

}
