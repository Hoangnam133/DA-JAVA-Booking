package com.example.booking.repository;

import com.example.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByTokenAndEmail(String token,String email);
    List<User> findByRolesRoleId(int roleId);
    Optional<User> findByPhone(String phoneNumber);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = 'ADMIN'")
    Optional<User> findAdminUser();
}
