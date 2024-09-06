/**
 * 
 */
package com.sliit.spm.codecomplexityanalyzer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.sliit.spm.codecomplexityanalyzer.model.Project;

@Repository
public interface ProjectRepo extends MongoRepository<Project, String> {

	//public List<Project> findByOrderByCreatedTimeAsc();
}
