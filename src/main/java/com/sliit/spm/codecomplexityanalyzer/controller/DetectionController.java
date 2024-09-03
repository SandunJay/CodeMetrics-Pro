package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.ProjectInfo;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.DetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("")
public class DetectionController {
    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<ProjectInfo>> analyzeProjects(@RequestParam String folderPath) {
        List<ProjectInfo> projectInfoList = detectionService.analyzeProjects(folderPath);
        if (projectInfoList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(projectInfoList);
    }
}
