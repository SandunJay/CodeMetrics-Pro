package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.model.ProjectInfo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetectionService {

    public List<ProjectInfo> analyzeProjects(String folderPath) {
        File rootFolder = new File(folderPath);
        List<ProjectInfo> projects = new ArrayList<>();

        if (rootFolder.exists() && rootFolder.isDirectory()) {
            File[] subFolders = rootFolder.listFiles(File::isDirectory);
            if (subFolders != null) {
                for (File subFolder : subFolders) {
                    String language = detectLanguage(subFolder);
                    if (language != null) {
                        projects.add(new ProjectInfo(subFolder.getName(), language));
                    }
                }
            }
        }
        return projects;
    }

    private String detectLanguage(File projectFolder) {
        File[] files = projectFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".java") || fileName.equals("pom.xml") || fileName.equals("build.gradle")) {
                    return "Java";
                } else if (fileName.endsWith(".js") || fileName.endsWith(".ts") || fileName.equals("package.json")) {
                    return "JavaScript/TypeScript";
                } else if (fileName.endsWith(".go") || fileName.equals("go.mod")) {
                    return "Golang";
                } else if (fileName.endsWith(".py") || fileName.equals("requirements.txt")) {
                    return "Python";
                } else if (fileName.endsWith(".cpp") || fileName.endsWith(".h") || fileName.endsWith(".c")) {
                    return "C++";
                } else if (fileName.endsWith(".kt")) {
                    return "Kotlin";
                } else if (fileName.endsWith(".rb") || fileName.equals("Gemfile")) {
                    return "Ruby";
                }
            }
        }
        return null;
    }
}
