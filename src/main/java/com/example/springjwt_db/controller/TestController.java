package com.example.springjwt_db.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/auth")
    public ResponseEntity<?> testAuth() {
        log.info("Testando autenticação");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (auth != null) {
            response.put("isAuthenticated", auth.isAuthenticated());
            response.put("principal", auth.getPrincipal().toString());
            response.put("name", auth.getName());
            response.put("authorities", auth.getAuthorities().toString());
            
            log.info("Autenticação encontrada: {}", auth);
            return ResponseEntity.ok(response);
        } else {
            log.warn("Nenhuma autenticação encontrada no contexto");
            response.put("isAuthenticated", false);
            response.put("message", "Nenhuma autenticação encontrada");
            return ResponseEntity.status(401).body(response);
        }
    }
    
    @GetMapping("/public")
    public ResponseEntity<?> publicTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este é um endpoint público de teste");
        return ResponseEntity.ok(response);
    }
} 