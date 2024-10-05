package com.sliit.spm.codecomplexityanalyzer.auth;

import com.sliit.spm.codecomplexityanalyzer.model.User;
import com.sliit.spm.codecomplexityanalyzer.repository.UserRepository;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User registerUser(User user) throws Exception {
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new Exception("Email is already in use.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public void sendResetPasswordEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            user.get().setResetToken(token);
            userRepository.save(user.get());
            emailService.sendPasswordResetEmail(email, token);
        }
    }

    public void resetPassword(String token, String newPassword) {
        Optional<User> user = userRepository.findByResetToken(token);
        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(newPassword));
            user.get().setResetToken(null);
            userRepository.save(user.get());
        }
    }
}
