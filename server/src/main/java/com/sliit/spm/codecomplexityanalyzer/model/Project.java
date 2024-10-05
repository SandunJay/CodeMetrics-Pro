package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "project")
public class Project {

    @Id
    private String id;
    private String user;
    private String name;
    private String projectKey;
    private String sourcePath;
    private String language;
    private List<ProjectFile> files;
    private int cp;
    private Set<String> patterns = new HashSet<>();
    private AiAnalysisResponse response;


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
