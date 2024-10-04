package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectFile {

    private String relativePath;
    private List<Line> linesData;
    private int cp;

    @Override
    public String toString() {
        return "ProjectFile{" +
                "relativePath='" + relativePath + '\'' +
                ", linesData=" + linesData +
                ", cp=" + cp +
                '}';
    }
}
