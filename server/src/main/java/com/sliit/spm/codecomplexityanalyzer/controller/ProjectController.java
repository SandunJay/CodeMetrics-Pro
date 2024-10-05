/**
 *
 */
package com.sliit.spm.codecomplexityanalyzer.controller;

//import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ProjectServiceImpl;
import com.sliit.spm.codecomplexityanalyzer.dto.AnalyzedDataDto;
import com.sliit.spm.codecomplexityanalyzer.dto.ProjectDto;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sliit.spm.codecomplexityanalyzer.model.Project;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {
//	@Autowired
//	private ProjectServiceImpl projectService;

//	@PostMapping("/projects")
//	public ResponseEntity<?> saveProject(@RequestBody Project project) {
//		return new ResponseEntity<>(projectService.save(project), HttpStatus.OK);
//	}
//
//	@GetMapping("/projects/{key}")
//	public ResponseEntity<?> getProject(@PathVariable("key") String key) {
//		return new ResponseEntity<>(projectService.getByKey(key), HttpStatus.OK);
//	}
//
//	@GetMapping("/projects/{key}/history")
//	public ResponseEntity<?> getProjectHistory(@PathVariable("key") String key) {
//		return new ResponseEntity<>(projectService.getHistoryByKey(key), HttpStatus.OK);
//	}
//
//	@GetMapping("/projects")
//	public ResponseEntity<?> getProjects() {
//		return new ResponseEntity<>(projectService.getAll(), HttpStatus.OK);
//	}

    private final ProjectServiceImpl projectService;

    public ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    // Create a new project
    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestBody ProjectDto projectDto) {
        Project createdProject = projectService.createProject(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    // Update project with analysis (after zip or pdf processing)
    @PutMapping("/{id}/update")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody AnalyzedDataDto analyzedDataDto) {
        Project updatedProject = projectService.updateProject(id, analyzedDataDto);
        return ResponseEntity.ok(updatedProject);
    }

    // Delete a project
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Fetch a project by ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProject(@PathVariable String id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
}
