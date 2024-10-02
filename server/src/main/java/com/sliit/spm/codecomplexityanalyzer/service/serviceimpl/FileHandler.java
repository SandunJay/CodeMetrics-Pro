package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.model.Line;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.model.Stack;
import com.sliit.spm.codecomplexityanalyzer.model.ProjectFile;
import com.sliit.spm.codecomplexityanalyzer.repository.ProjectRepository;
//import com.sliit.spm.codecomplexityanalyzer.utils.Client;
import com.sliit.spm.codecomplexityanalyzer.utils.MethodAndVariableFinder;
import com.sliit.spm.codecomplexityanalyzer.utils.RecursiveMethodLineNumberFinder;
import org.apache.commons.io.FilenameUtils;
import com.sliit.spm.codecomplexityanalyzer.service.analyzer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class FileHandler {


    public static Stack stack;
    private Project project;
    private String projectRoot = "";
    private List<File> fileList = new ArrayList<>();
    private List<ProjectFile> projectFiles = new ArrayList<>();

    @Autowired
    private ProjectRepository projectRepository;

    public void readFiles(Project p) throws Exception {
        this.project = p;
        this.projectRoot = project.getSourcePath();

        getFiles(projectRoot);
        System.out.println(fileList);
        calculateComplexity();
        this.project.setFiles(projectFiles);

        // Calculate project cp
        int projectCp = 0;
        for (ProjectFile pf : projectFiles) {
            projectCp += pf.getCp();
        }
        project.setCp(projectCp);

        updateProjectData(project);
//        Client.sendAnalysisData(project);
    }

    private void updateProjectData(Project project) throws Exception {
        try {
            Project project1 = projectRepository.findById(project.getId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            project1.setLanguage(project.getLanguage());
            project1.setPatterns(project.getPatterns());
            project1.setResponse(project.getResponse());
            project1.setCp(project.getCp());
            projectRepository.save(project1);
        }catch (Exception e){
            throw new Exception("Exception occurred");
        }
    }

    public void getFiles(String projectPath) {
        File dir = new File(projectPath);
        File[] directoryListing = dir.listFiles();
        if (Objects.nonNull(directoryListing)) {
            for (File file : directoryListing) {
                if (file.isDirectory()) {
                    getFiles(file.getPath());
                }
                if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("java")) {
                    fileList.add(file);
                    project.setLanguage("Java");
                } else if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("cpp")) {
                    fileList.add(file);
                    project.setLanguage("C++");
                }
            }
        }
    }

    private void calculateComplexity() {
        System.out.println("Found " + fileList.size() + " Files in source path");
        fileList.forEach(file -> {
            ProjectFile projectFile = new ProjectFile();
            stack = new Stack();

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {

                System.out.println("Analyzing file " + file.getCanonicalPath().replace(projectRoot, ""));
                projectFile.setRelativePath(file.getCanonicalPath().replace(projectRoot, ""));
                List<Line> lines = new ArrayList<>();
                boolean singleLineCommented;
                boolean multiLineCommented = false;

                // helper for Cs calculation
                List<String> methodsAndVariables = MethodAndVariableFinder.getMethodAndVariables(file);
                HashMap<Integer, Integer> recursiveLineNumbers = RecursiveMethodLineNumberFinder.getRecursiveMethodLineNumbers(file);

                for (String line; (line = lnr.readLine()) != null; ) {
                    Line lineObj = new Line();
                    lineObj.setLineNo(lnr.getLineNumber());
                    lineObj.setData(line);

                    // ignore comment lines
                    if (line.trim().startsWith("//") || line.trim().startsWith("import") || line.trim().startsWith("include")) {
                        singleLineCommented = true;
                    } else {
                        singleLineCommented = false;
                    }
                    if (line.trim().startsWith("/*")) {
                        multiLineCommented = true;
                    }
                    if (line.trim().startsWith("*/")) {
                        line = line.replaceFirst("\\*/", "");
                        multiLineCommented = false;
                    }
                    if (line.contains("//")) {
                        line = line.replace(line.substring(line.indexOf("//")), "");
                    }

                    //calculate complexity if line is not commented
                    if (!singleLineCommented && !multiLineCommented) {
                        Cs.calcCs(lineObj, line, methodsAndVariables);
                        Ci.calcCi(lineObj, line, project.getLanguage());
                        Ctc.calcCtc(lineObj, line);
                        Cnc.calcCnc(lineObj, line);
                        Cr.calcCr(lineObj, recursiveLineNumbers);
                    }

                    if (line.trim().endsWith("*/")) {
                        multiLineCommented = false;
                    }

                    lines.add(lineObj);
                }

                int fileCp = 0;
                for (Line line : lines) {
                    if (line.getCr() != 0) {
                        fileCp += line.getCr();
                    } else {
                        fileCp += line.getCps();
                    }
                }

                projectFile.setCp(fileCp);
                projectFile.setLinesData(lines);

                projectFiles.add(projectFile);
                RecursiveMethodLineNumberFinder.resetData();
                Ci.resetCi(); //reset ci value after file ends
                Ctc.setSwitchCtc(); //add switch ctc value

            } catch (IOException e) {
                System.out.println("Error reading file"+ e);
            }
        });
    }
}
