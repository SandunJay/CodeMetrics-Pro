//package com.sliit.spm.codecomplexityanalyzer.controller;
//
//import com.sliit.spm.codecomplexityanalyzer.model.User;
//import com.sliit.spm.codecomplexityanalyzer.service.auth.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class AuthController {
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<String> register(@RequestBody User userDto) {
//        userService.registerUser(userDto);
//        return ResponseEntity.ok("User registered successfully");
//    }
//
////    @PostMapping("/login")
////    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
////        String token = userService.login(loginDto.getEmail(), loginDto.getPassword());
////        return ResponseEntity.ok(token);
////    }
//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
//        userService.sendTemporaryPassword(email);
//        return ResponseEntity.ok("Temporary password sent to your email");
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
//        userService.resetPassword(email, newPassword);
//        return ResponseEntity.ok("Password reset successfully");
//    }
//}
