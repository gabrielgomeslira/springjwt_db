package com.example.springjwt_db.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@AllArgsConstructor
@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
}
