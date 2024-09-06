package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.DetectionService;
//import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.FileHandlerService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ImageScanner;
import com.sliit.spm.codecomplexityanalyzer.utils.ErrorMessages;
import com.sliit.spm.codecomplexityanalyzer.utils.LanguageDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/detect")
public class DetectionController {
    @Autowired
    private LanguageDetector languageDetector;
    private final ImageScanner imageScanner;
//    private final FileHandlerService fileHandlerService;
    public DetectionController(ImageScanner imageScanner
//            , FileHandlerService fileHandlerService
    ) {
//        this.fileHandlerService = fileHandlerService;
        this.imageScanner = imageScanner;
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<Project>> analyzeProjects(@RequestParam String folderPath) {
        List<Project> projectInfoList = languageDetector.analyzeProjects(folderPath);
        if (projectInfoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(projectInfoList);
    }

//    To detect a code in Local storage and read its directories and subdirectories
    @GetMapping("/analyze-base")
    public void analyzeBaseProject(@RequestParam String path){
        Project project = new Project();
        project.setProjectKey("ACC");
        if (!path.isEmpty()) {
            project.setSourcePath(path);
        } else {
            throw new NoSuchElementException(ErrorMessages.SP_NOT_FOUND_ERR);
        }
//        fileHandlerService.readFiles(project);
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


}
