/**
 *
 */
package com.sliit.spm.codecomplexityanalyzer.repository;

import com.sliit.spm.codecomplexityanalyzer.model.Analysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AnalysisRepo extends MongoRepository<Analysis, String> {
    Optional<Analysis> findByProjectKey(String projectKey);

	Optional<List<Analysis>> findFirst10ByOrderByCreatedTimeDesc(String projectKey);

}
