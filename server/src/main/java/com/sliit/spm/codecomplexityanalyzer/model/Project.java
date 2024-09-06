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

    public List<ProjectFile> getFiles() {
        return files;
    }

    public void setFiles(List<ProjectFile> files) {
        this.files = files;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCp() {
        return cp;
    }

    public void setCp(int cp) {
        this.cp = cp;
    }

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
