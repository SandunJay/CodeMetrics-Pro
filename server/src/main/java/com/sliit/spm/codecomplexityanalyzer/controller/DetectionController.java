package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.DesignPatternDetectionService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.FileHandler;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ImageScanner;
import com.sliit.spm.codecomplexityanalyzer.utils.ErrorMessages;
import com.sliit.spm.codecomplexityanalyzer.utils.LanguageDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/detect")
public class DetectionController {
    @Autowired
    private LanguageDetector languageDetector;
    private final ImageScanner imageScanner;
    private final FileHandler fileHandlerService;
    public DetectionController(ImageScanner imageScanner, FileHandler fileHandlerService
    ) {
        this.fileHandlerService = fileHandlerService;
        this.imageScanner = imageScanner;
    }

//    @GetMapping("/analyze")
//    public ResponseEntity<List<Project>> analyzeProjects(@RequestParam String folderPath) {
//        List<Project> projectInfoList = languageDetector.analyzeProjects(folderPath);
//        if (projectInfoList.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(projectInfoList);
//    }

//    To detect a code in Local storage and read its directories and subdirectories
    @GetMapping("/analyze")
    public ResponseEntity<List<Project>> analyzeBaseProject(@RequestParam String filePath){
        List<Project> projectList = languageDetector.analyzeProjects(filePath);
        if (projectList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Project project = new Project();
        project.setProjectKey("ACC");
        if (!filePath.isEmpty()) {
            project.setSourcePath(filePath);
        } else {
            throw new NoSuchElementException(ErrorMessages.SP_NOT_FOUND_ERR);
        }
        fileHandlerService.readFiles(project);
        return null;
    }

//    To get the code from an image in local storage
    @GetMapping("/extract")
    public ResponseEntity<String> extractText(@RequestParam String filePath) {
        Project project = new Project();
        if (filePath.isEmpty()){
            throw new  NullPointerException("File Path cannot be null");
        }
        try {
            String decodedPath = URLDecoder.decode(filePath, "UTF-8");
            project.setSourcePath(decodedPath);
            String text = imageScanner.readImage(project);
            return ResponseEntity.ok(text);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body("Error decoding file path: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error extracting text: " + e.getMessage());
        }
    }

//  Below methods are generated for detecting design patterns
    @Autowired
    private DesignPatternDetectionService detectionService;

    @PostMapping("/detect")
    public ResponseEntity<Map<String, Boolean>> detectPatterns(@RequestParam("classFile") MultipartFile classFile) throws ClassNotFoundException {
        // Load the class from the file (this is a placeholder)
        Class<?> clazz = loadClassFromFile(classFile);
        Map<String, Boolean> patterns = detectionService.detectPatterns(clazz);
        return ResponseEntity.ok(patterns);
    }

    @PostMapping("/detect-from-dir")
    public ResponseEntity<Map<String, Map<String, Boolean>>> detectPatternsFromDirectory(@RequestParam("directoryPath") String directoryPath) throws ClassNotFoundException, IOException {
        Map<String, Map<String, Boolean>> results = detectionService.detectPatternsFromDirectory(directoryPath);
        return ResponseEntity.ok(results);
    }

    private Class<?> loadClassFromFile(MultipartFile file) throws ClassNotFoundException {
        // Implement loading logic for class from file
        return null;
    }
}
