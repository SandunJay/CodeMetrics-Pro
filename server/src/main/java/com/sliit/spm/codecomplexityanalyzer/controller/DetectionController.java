package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.model.ProjectInfo;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.DetectionService;
//import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.FileHandlerService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ImageScanner;
import com.sliit.spm.codecomplexityanalyzer.utils.ErrorMessages;
import net.sourceforge.tess4j.TesseractException;
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
    private final DetectionService detectionService;
    private final ImageScanner imageScanner;
//    private final FileHandlerService fileHandlerService;
    public DetectionController(DetectionService detectionService, ImageScanner imageScanner
//            , FileHandlerService fileHandlerService
    ) {
        this.detectionService = detectionService;
//        this.fileHandlerService = fileHandlerService;
        this.imageScanner = imageScanner;
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<ProjectInfo>> analyzeProjects(@RequestParam String folderPath) {
        List<ProjectInfo> projectInfoList = detectionService.analyzeProjects(folderPath);
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
        try {
            String decodedPath = URLDecoder.decode(filePath, "UTF-8");
            String text = imageScanner.readImage(decodedPath);
            return ResponseEntity.ok(text);
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body("Error decoding file path: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error extracting text: " + e.getMessage());
        }
    }


}
