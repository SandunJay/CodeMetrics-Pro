package com.sliit.spm.codecomplexityanalyzer.repository;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByUser(String user);
}
