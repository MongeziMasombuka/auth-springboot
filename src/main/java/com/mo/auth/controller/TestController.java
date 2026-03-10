package com.mo.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello! If you see this, your JWT is valid and working.");
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails contains the username extracted from the verified JWT
        String username = userDetails.getUsername();

        // Now you can use this username to fetch user-specific data from your database!
        return ResponseEntity.ok("Welcome to your profile, " + username + "!");
    }
}
