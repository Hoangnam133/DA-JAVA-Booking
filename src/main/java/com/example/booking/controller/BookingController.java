package com.example.booking.controller;

import com.example.booking.entity.Booking;
import com.example.booking.entity.Room;
import com.example.booking.entity.User;
import com.example.booking.service.BookingService;
import com.example.booking.service.MailService;
import com.example.booking.service.RoomService;
import com.example.booking.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;
    private final MailService mailService;

    public double gb_totalPrice = 0;
    @Autowired
    public BookingController(BookingService bookingService, UserService userService, RoomService roomService, MailService mailService){
        this.bookingService = bookingService;
        this.userService = userService;
        this.roomService = roomService;
        this.mailService = mailService;
    }
    //for admin
    @GetMapping("/searchBookingByUserPhone")
    public String getBooking(@RequestParam String phone, Model model) {
        try {
            List<Booking> bookings = bookingService.findBookingByPhone(phone);
            if(bookings.isEmpty()){
                model.addAttribute("errorMessage", "Booking not found");
            }
            else {
                model.addAttribute("bookings", bookings);
            }
            model.addAttribute("bookings", bookings);
            return "ListOfAdmin/listBooking";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Booking not found");
            return "errorPage";
        }
    }
    @GetMapping("/searchBookingConfirmByUserPhone")
    public String getBookingConfirm(@RequestParam String phone, Model model) {
        try {
            List<Booking> bookings = bookingService.findBookingConfirmByPhone(phone);
            if(bookings.isEmpty()){
                model.addAttribute("errorMessage", "Booking not found");
            }
            else {
                model.addAttribute("bookings", bookings);
            }
            model.addAttribute("bookings", bookings);
            return "ListOfAdmin/listBookingChecked";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Booking not found");
            return "errorPage";
        }
    }
    @GetMapping("/listBookingOfAdmin")
    public String getAllBookingOfAdmin(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Pageable pageable = PageRequest.of(page, 4);
            Page<Booking> bookingPage = bookingService.showBookingListOfAdmin(pageable);
            model.addAttribute("bookings", bookingPage.getContent());
            model.addAttribute("totalPages", bookingPage.getTotalPages());
            model.addAttribute("totalPages", bookingPage.getTotalPages());
            model.addAttribute("currentPage", page);
            return "ListOfAdmin/listBooking";
        } catch (Exception e) {
            model.addAttribute("Errors", e);
            return "errors";
        }
    }
    // for admin
    @GetMapping("/listBookingCheckedOfAdmin")
    public String getAllBookingIsChecked(@RequestParam(defaultValue = "0") int page, Model model){
        try {
            Pageable pageable = PageRequest.of(page, 4);
            Page<Booking> bookingPage = bookingService.showBookingListCheckedOfAdmin(pageable);
            model.addAttribute("bookings", bookingPage.getContent());
            model.addAttribute("totalPages", bookingPage.getTotalPages());
            model.addAttribute("totalPages", bookingPage.getTotalPages());
            model.addAttribute("currentPage", page);
            return "ListOfAdmin/listBookingChecked";
        }catch (Exception e){
            model.addAttribute("Errors",e);
            return "errors";
        }
    }
    // for admin
    @GetMapping("/BookingUpdateCheckIn/{bookingId}")
    public String userRequiresRegistration(@PathVariable("bookingId") int bookingId, Model model){
        try {
            Booking existingBooking = bookingService.findBookingById(bookingId);
            model.addAttribute("booking",existingBooking);
            return "Bookings/requiresCheckIn";
        }catch (Exception e){
            model.addAttribute("errors",e);
            return "errors";
        }
    }
    @PostMapping("/saveBookingUpdateCheckIn/{bookingId}")
    public String saveUserRequiresRegistration(@PathVariable("bookingId") int bookingId, Booking booking, Model model, RedirectAttributes redirectAttributes){
               try {
                   Booking existingBooking = bookingService.findBookingById(bookingId);
                   existingBooking.setCheckInStatus(true);
                   bookingService.checkIn(existingBooking);
                   redirectAttributes.addFlashAttribute("successMessage", "Check-in confirmed successfully!");
                   return "redirect:/bookings/listBookingCheckedOfAdmin";
               }catch (Exception e){
                   model.addAttribute("errors",e);
                   return "errors";
               }
    }
    // for user
    @GetMapping("/bookingUpdateIsCanceled/{bookingId}")
    public String userRequestCancel(@PathVariable("bookingId") int bookingId, Model model){
        try {
            Booking existingBooking = bookingService.findBookingById(bookingId);
            model.addAttribute("booking",existingBooking);
            return "Bookings/RequestCancel";
        }catch (Exception e){
            model.addAttribute("errors",e);
            return "errors";
        }
    }
    // for user
    @PostMapping("/SaveBookingUpdateIsCanceled/{bookingId}")
    public String saveUserRequestCancel(@PathVariable("bookingId") int bookingId,
                                        @RequestParam String reasonCancel,
                                        Booking booking, BindingResult result,
                                        @AuthenticationPrincipal UserDetails userDetails){
        if (result.hasErrors()) {
            booking.setBookingId(bookingId);
        }
        bookingService.cancelBooking(booking,reasonCancel);
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return "redirect:/bookings/listCancelBookingOfUser";
    }
    @GetMapping("/AvailableRooms")
    public String searchAvailableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                       @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                       Model model) {
        List<Room> rooms = roomService.availableRooms(checkInDate, checkOutDate);
        model.addAttribute("rooms", rooms);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        return "Bookings/availableRooms";
    }
    @GetMapping("/add/{roomId}")
    public String createFromBooking(@PathVariable("roomId") int roomId,
                                    @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                    @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        try {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Booking booking = new Booking();
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);
            booking.setUser(user);
            Room room = roomService.searchRoom(roomId);
            if (room == null) {
                throw new RuntimeException("Room not found roomId: " + roomId);
            }
            long numberOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            if(numberOfDays == 0){
                booking.setTotalPrice(room.getPrice());
                gb_totalPrice = room.getPrice();
            }
            else {
                booking.setTotalPrice(room.getPrice() * numberOfDays);
                gb_totalPrice = room.getPrice() * numberOfDays;
            }

            booking.setRoom(room);


            model.addAttribute("booking", booking);

            return "Bookings/add";
        } catch (Exception e) {
            model.addAttribute("errors", e);
            return "Errors"; // Ensure you have a view "Errors.html" or similar
        }
    }

    @PostMapping("/save")
    public String saveCreateFromBooking(Booking booking,BindingResult result,
                                        @AuthenticationPrincipal UserDetails userDetails, Model model) throws MessagingException {
        if (result.hasErrors()) {
            return "/Bookings/add";
        }
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        booking.setUser(user);
        booking.setTotalPrice(gb_totalPrice);
        String pin = bookingService.generateRandomString();
        booking.setPin(pin);
        bookingService.createBooking(booking);
        sendBookingToEmail(booking,user);
        return "redirect:/homeUser" ;
    }
    // for user
    @GetMapping("/listBookingOfUser")
    public String getAllBookingOfUser(@AuthenticationPrincipal UserDetails userDetails, Model model){
        try {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            model.addAttribute("bookings",bookingService.ShowBookingListOfUser(user.getId()));
            return "Bookings/listBookingUser";
        }catch (Exception e){
            model.addAttribute("Errors",e);
            return "errors";
        }
    }
    public void sendBookingToEmail(Booking booking, User user) throws MessagingException {
        String subject = "Booking Confirmation";
        String htmlBody = "<div style='border: 3px solid #ccc; border-radius: 10px; overflow: hidden;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px;'>" +
                "<h2 style='color: #007bff; margin-bottom: 10px;'>Booking Confirmation</h2>" +
                "<p>Dear " + user.getUsername() + ",</p>" +
                "<p>Your booking has been confirmed.</p>" +
                "<h3>Booking Details:</h3>" +
                "<ul style='list-style-type: none; padding-left: 0;'>" +
                "<li><strong>Check-in Date:</strong> " + booking.getCheckInDate() + "</li>" +
                "<li><strong>Check-out Date:</strong> " + booking.getCheckOutDate() + "</li>" +
                "<li><strong>Total Price:</strong> $" + booking.getTotalPrice() + "</li>" +
                "<li><strong>Pin:</strong> " + booking.getPin() + "</li>" +
                "</ul>" +
                "<p>Thank you for booking with us!</p>" +
                "</div>" +
                "</div>";
            mailService.sendMail(user.getEmail(), subject, htmlBody);
    }

}
