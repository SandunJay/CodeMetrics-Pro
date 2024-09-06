package com.sliit.spm.codecomplexityanalyzer.repository;

import com.sliit.spm.codecomplexityanalyzer.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
