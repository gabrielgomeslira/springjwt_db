package com.example.springjwt_db.controller;

import com.example.springjwt_db.dto.MessageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    @GetMapping("/hello")
    public MessageResponse sayHello() {
        return new MessageResponse("Hello World");
    }
}
