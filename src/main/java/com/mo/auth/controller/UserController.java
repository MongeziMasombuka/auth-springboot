package com.mo.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<String> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails contains the username extracted from the verified JWT
        String username = userDetails.getUsername();

        // Now you can use this username to fetch user-specific data from your database!
        return ResponseEntity.ok("Welcome to your profile, " + username + "!");
    }
}
