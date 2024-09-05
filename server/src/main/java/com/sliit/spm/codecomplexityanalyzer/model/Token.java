package com.sliit.spm.codecomplexityanalyzer.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String token;

    private LocalDateTime expiryDate;

    private boolean isValid;
}
