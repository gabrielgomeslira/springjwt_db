package com.example.springjwt_db.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminOnlyEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este endpoint só pode ser acessado por administradores");
        return response;
    }
} 