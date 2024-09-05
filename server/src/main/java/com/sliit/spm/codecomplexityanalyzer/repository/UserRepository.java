package com.sliit.spm.codecomplexityanalyzer.repository;

import com.sliit.spm.codecomplexityanalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
