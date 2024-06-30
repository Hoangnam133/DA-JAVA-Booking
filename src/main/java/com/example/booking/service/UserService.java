package com.example.booking.service;


import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.RoleRepository;
import com.example.booking.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.booking.eRole.RoleType;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu.
    public void save(@NotNull User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }
    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng.
    public void setDefaultRole(String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.getRoles().add(roleRepository.findRoleByRoleId(RoleType.USER.value));
                    userRepository.save(user);
                },
                () -> { throw new UsernameNotFoundException("User not found"); }
        );}
    // Tải thông tin chi tiết người dùng để xác thực.
//    @Override
//    public UserDetails loadUserByUsername(String username) throws
//            UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getUsername())
//                .password(user.getPassword())
//                .authorities(user.getAuthorities())
//                .accountExpired(!user.isAccountNonExpired())
//                .accountLocked(!user.isAccountNonLocked())
//                .credentialsExpired(!user.isCredentialsNonExpired())
//                .disabled(!user.isEnabled())
//                .build();
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isAccountStatus()) {
            throw new DisabledException("User account is disabled");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(!user.isAccountNonExpired())
                .accountLocked(!user.isAccountNonLocked())
                .credentialsExpired(!user.isCredentialsNonExpired())
                .disabled(!user.isEnabled())
                .build();
    }
    // Tìm kiếm người dùng dựa trên tên đăng nhập.
    public Optional<User> findByUsername(String username) throws
            UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    public boolean checkPassword(User user, String password){
        return passwordEncoder.matches(password, user.getPassword());
    }
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    public static String generateToken() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
    @Value("${spring.mail.username}")
    private String fromEmail;

    // Other existing methods

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }
    public void createEmployeeAccount(User employee) {
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        Role employeeRole = roleRepository.findRoleByRoleId(RoleType.EMPLOYEE.value);
        if (employeeRole == null) {
            throw new RuntimeException("EMPLOYEE role not found. Make sure it's defined in the roles table.");
        }
        employee.getRoles().clear();
        employee.getRoles().add(employeeRole);
        userRepository.save(employee);

    }
    public List<User> findAllRoleId() {
        return userRepository.findByRolesRoleId(3);
    }
    @Transactional
    public void incrementLoginCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user != null) {
            user.setCountLogin(user.getCountLogin() + 1);
            userRepository.save(user);
        }
    }
    public int getTotalCountLogin(){
        return userRepository.findAll().stream().mapToInt(User::getCountLogin).sum();
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

}
