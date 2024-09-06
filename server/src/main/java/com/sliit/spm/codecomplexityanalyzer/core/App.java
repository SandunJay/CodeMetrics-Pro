package com.sliit.spm.codecomplexityanalyzer.core;

import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.FileHandler;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.utils.ErrorMessages;

import java.util.NoSuchElementException;
import java.util.Optional;

public class App {

    static void execute(Project project) {

        new FileHandler().readFiles(project);
    }

    public static void main(String[] args) {


        Optional<String> projectKey = Optional.ofNullable(System.getProperty("projectKey"));
        Optional<String> sourcePath = Optional.ofNullable(System.getProperty("sourcePath"));

        Project project = new Project();
        if (projectKey.isPresent()) {
            project.setProjectKey(projectKey.get());
        } else {
            // Removed logging for error
            throw new NoSuchElementException(ErrorMessages.PK_NOT_FOUND_ERR);
        }
        if (sourcePath.isPresent()) {
            project.setSourcePath(sourcePath.get());
        } else {
            // Removed logging for error
            throw new NoSuchElementException(ErrorMessages.SP_NOT_FOUND_ERR);
        }

        execute(project);
    }
}
