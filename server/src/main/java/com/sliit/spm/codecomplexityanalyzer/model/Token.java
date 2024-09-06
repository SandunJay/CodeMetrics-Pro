package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document("token")
public class Token {
    @Id
    private Long id;
    private User user;
    private String token;
    private LocalDateTime expiryDate;
    private boolean isValid;
}
