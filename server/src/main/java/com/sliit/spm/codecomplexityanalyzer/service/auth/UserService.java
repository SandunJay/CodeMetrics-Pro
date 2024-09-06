//package com.sliit.spm.codecomplexityanalyzer.service.auth;
//
//import com.sliit.spm.codecomplexityanalyzer.model.User;
//import com.sliit.spm.codecomplexityanalyzer.repository.UserRepository;
//import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//@Service
//public class UserService {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtTokenProvider tokenProvider;
//
//    private final EmailService emailService;
//
//    public UserService(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    public String login(String email, String password) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new RuntimeException("Invalid email or password");
//        }
//
//        if (user.isTemporaryPassword()) {
//            throw new RuntimeException("Please reset your password first.");
//        }
//
//        return tokenProvider.generateToken(user.getEmail());
//    }
//
//    public void registerUser(User userDto) {
//        if (userRepository.existsByEmail(userDto.getEmail())) {
//            throw new RuntimeException("Email already in use");
//        }
//
//        User user = new User();
//        user.setEmail(userDto.getEmail());
//        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        user.setTemporaryPassword(false);
//        userRepository.save(user);
//
//        emailService.sendEmail(user.getEmail(), "Welcome", "Thank you for registering.");
//    }
//
//    public void sendTemporaryPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        String tempPassword = UUID.randomUUID().toString().substring(0, 8);  // Generate temp password
//        user.setPassword(passwordEncoder.encode(tempPassword));
//        user.setTemporaryPassword(true);
//        userRepository.save(user);
//
//        emailService.sendEmail(user.getEmail(), "Temporary Password", "Your temporary password is: " + tempPassword);
//    }
//
//    public void resetPassword(String email, String newPassword) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        user.setTemporaryPassword(false);
//        userRepository.save(user);
//    }
//}
