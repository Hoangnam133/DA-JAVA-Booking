package com.example.booking.controller;

import com.example.booking.entity.User;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class  UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "Users/login";
    }
    @GetMapping("/homeUser")
    public String getLayoutUser(){
            return "Users/home";
    }
    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new User()); // Thêm một đối tượng User mới vào model
        return "Users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, // Validate đối tượng User
                           @NotNull BindingResult bindingResult, // Kết quả của quá trình validate
                           Model model) {
        if (bindingResult.hasErrors()) { // Kiểm tra nếu có lỗi validate
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "Users/register"; // Trả về lại view "register" nếu có lỗi
        }
        userService.save(user); // Lưu người dùng vào cơ sở dữ liệu
        userService.setDefaultRole(user.getUsername()); // Gán vai trò mặc định cho người dùng
        return "redirect:/login"; // Chuyển hướng người dùng tới trang "login"
    }

    @GetMapping("/informationUser")
    public String currentUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            model.addAttribute("user", user);
            return "Users/information";
        }
        return "redirect:/login";
    }
    @GetMapping("/changePassword")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            if (userDetails != null) {
                User existingUser = userService.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                model.addAttribute("user", existingUser);
                return "Users/changePassword";
            } else {
                return "redirect:/informationUser";
            }
        } catch (Exception e) {
            model.addAttribute("errors", e.getMessage());
            return "errorPage";
        }
    }
    @PostMapping("/savePassword")
    public String saveChangePassword(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam("currentPassword") String currentPassword,
                                     @RequestParam("newPassword") String newPassword,
                                     @RequestParam("confirmNewPassword") String confirmNewPassword,
                                     Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User existingUser = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.checkPassword(existingUser, currentPassword)) {
            model.addAttribute("error", "Current password is incorrect");
            return "Users/changePassword";
        }
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "Users/changePassword";
        }
        if (newPassword.length() < 6 || newPassword.length() > 250) {
            model.addAttribute("error", "Password must be between 6 and 250 characters");
            return "Users/changePassword";
        }
        userService.updatePassword(existingUser, newPassword);
        return "redirect:/informationUser";
    }
    @GetMapping("/createEmployeeAccount")
    public String showCreateEmployeeAccountForm(Model model) {
        model.addAttribute("employee", new User());
        return "Users/createEmployeeAccount";
    }

    @PostMapping("/saveCreateEmployeeAccount")
    public String createEmployeeAccount(@Valid @ModelAttribute("employee") User employee,
                                        BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "Users/createEmployeeAccount";
        }
        userService.createEmployeeAccount(employee);
        return "redirect:/employeeAccount";
    }
    @GetMapping("employeeAccount")
    public String showEmployeeAccountForm(Model model) {
        List<User> employees = userService.findAllRoleId();
        model.addAttribute("employees", employees);
        return "ListOfAdmin/ListEmployeeAccount";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getDetailEmployee/{userId}")
    public String getUser(@PathVariable Long userId, Model model) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            return "ListOfAdmin/employeeDetails"; // Thay đổi view name nếu cần thiết
        } else {
            model.addAttribute("message", "Không tìm thấy người dùng.");
            return "admin/error";
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/updateUserStatus/{userId}")
    public String updateUserStatus(@PathVariable Long userId, User user, Model model) {
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setAccountStatus(user.isAccountStatus());
            userService.saveUser(existingUser);
            model.addAttribute("message", "Cập nhật trạng thái tài khoản thành công.");
            model.addAttribute("user", user);
            return "redirect:/employeeAccount";
        } else {
            model.addAttribute("message", "Không tìm thấy người dùng.");
            return "admin/error";
        }
    }





}
