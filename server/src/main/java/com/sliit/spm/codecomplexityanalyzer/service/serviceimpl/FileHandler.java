package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.model.Line;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.model.Stack;
import com.sliit.spm.codecomplexityanalyzer.utils.Client;
import com.sliit.spm.codecomplexityanalyzer.model.ProjectFile;
import com.sliit.spm.codecomplexityanalyzer.utils.MethodAndVariableFinder;
import com.sliit.spm.codecomplexityanalyzer.utils.RecursiveMethodLineNumberFinder;
import org.apache.commons.io.FilenameUtils;
import com.sliit.spm.codecomplexityanalyzer.service.analyzer.*;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FileHandler {

    public static Stack stack;
    private Project project;
    private String projectRoot = "/Users/udeesharukshan/Documents/Algorithm-Complexity-Calculator/gg/code";
    private List<File> fileList = new ArrayList<>();
    private List<ProjectFile> projectFiles = new ArrayList<>();

    public void readFiles(Project p) {
        this.project = p;
        this.projectRoot = project.getSourcePath();

        getFiles(projectRoot);
        calculateComplexity();
        this.project.setFiles(projectFiles);

        // Calculate project cp
        int projectCp = 0;
        for (ProjectFile pf : projectFiles) {
            projectCp += pf.getCp();
        }
        project.setCp(projectCp);

        Client.sendAnalysisData(project);
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
         fileList.forEach(file -> {
            ProjectFile projectFile = new ProjectFile();
            stack = new Stack();

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {

                 projectFile.setRelativePath(file.getCanonicalPath().replace(projectRoot, ""));
                List<Line> lines = new ArrayList<>();
                boolean singleLineCommented = false;
                boolean multiLineCommented = false;

                // Helper for Cs calculation
                List<String> methodsAndVariables = MethodAndVariableFinder.getMethodAndVariables(file);
                HashMap<Integer, Integer> recursiveLineNumbers = RecursiveMethodLineNumberFinder.getRecursiveMethodLineNumbers(file);

                for (String line; (line = lnr.readLine()) != null; ) {
                    Line lineObj = new Line();
                    lineObj.setLineNo(lnr.getLineNumber());
                    lineObj.setData(line);

                    // Handle single-line comments
                    if (line.trim().startsWith("//")) {
                        singleLineCommented = true;
                        lineObj.setSingleLineCommentsCount(lineObj.getSingleLineCommentsCount() + 1);
                    } else {
                        singleLineCommented = false;
                    }

                    // Handle multi-line comments
                    if (line.trim().startsWith("/*")) {
                        multiLineCommented = true;
                        lineObj.setMultiLineCommentsCount(lineObj.getMultiLineCommentsCount() + 1);
                    }
                    if (line.trim().endsWith("*/")) {
                        multiLineCommented = false;

                    }

                    // Remove comment part from the line data for complexity calculations
                    if (line.contains("//")) {
                        line = line.substring(0, line.indexOf("//"));
                    }

                    // Calculate complexity if line is not commented
                    if (!singleLineCommented && !multiLineCommented) {
                        Cs.calcCs(lineObj, line, methodsAndVariables);
                        Ci.calcCi(lineObj, line, project.getLanguage());
                        Ctc.calcCtc(lineObj, line);
                        Cnc.calcCnc(lineObj, line);
                        Cr.calcCr(lineObj, recursiveLineNumbers);
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
                Ci.resetCi(); // Reset ci value after file ends
                Ctc.setSwitchCtc(); // Add switch ctc value

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
