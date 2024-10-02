package com.sliit.spm.codecomplexityanalyzer.repository;

import com.sliit.spm.codecomplexityanalyzer.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
}
