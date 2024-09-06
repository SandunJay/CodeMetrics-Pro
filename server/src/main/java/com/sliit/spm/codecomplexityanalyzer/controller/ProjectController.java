/**
 * 
 */
package com.sliit.spm.codecomplexityanalyzer.controller;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sliit.spm.codecomplexityanalyzer.model.Project; 

@CrossOrigin(origins = "*")
@RestController
public class ProjectController {
	@Autowired
	ProjectService projectService;

	@PostMapping("/projects")
	public ResponseEntity<?> saveProject(@RequestBody Project project) {
		return new ResponseEntity<>(projectService.save(project), HttpStatus.OK);
	}

	@GetMapping("/projects/{key}")
	public ResponseEntity<?> getProject(@PathVariable("key") String key) {
		return new ResponseEntity<>(projectService.getByKey(key), HttpStatus.OK);
	}

	@GetMapping("/projects/{key}/history")
	public ResponseEntity<?> getProjectHistory(@PathVariable("key") String key) {
		return new ResponseEntity<>(projectService.getHistoryByKey(key), HttpStatus.OK);
	}

	@GetMapping("/projects")
	public ResponseEntity<?> getProjects() {
		return new ResponseEntity<>(projectService.getAll(), HttpStatus.OK);
	}
}
