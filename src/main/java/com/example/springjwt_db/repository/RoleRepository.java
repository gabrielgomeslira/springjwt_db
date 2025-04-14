package com.example.springjwt_db.repository;

import com.example.springjwt_db.entity.ERole;
import com.example.springjwt_db.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
