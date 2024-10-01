package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<String> patterns = new HashSet<>();
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
