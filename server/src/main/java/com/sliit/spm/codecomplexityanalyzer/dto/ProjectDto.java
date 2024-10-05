package com.sliit.spm.codecomplexityanalyzer.dto;

import lombok.Data;

@Data
public class ProjectDto {
    private String email; // Refers to user ID from the user model
    private String name;
//    private String projectKey; // Optional, can be generated
}

