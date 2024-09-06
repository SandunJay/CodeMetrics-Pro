/**
 *
 */
package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;


import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.model.Analysis;

import java.util.List;
import java.util.Optional;


public interface ProjectService {
	public Project save(Project project);

	public Optional<Project> getByKey(String projectKey);

	public List<Project> getAll();

	public List<Analysis> getHistoryByKey(String key);
}
