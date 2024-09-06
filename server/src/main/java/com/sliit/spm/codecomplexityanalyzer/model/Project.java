package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Project {

    private String name;
    private String projectKey;
    private String sourcePath;
    private String language;
    private List<ProjectFile> files;
    private int cp;

    @Override
    public String toString() {
        return "Project{" +
                "projectKey='" + projectKey + '\'' +
                ", sourcePath='" + sourcePath + '\'' +
                ", language='" + language + '\'' +
                ", files=" + files +
                ", cp=" + cp +
                '}';
    }
}
