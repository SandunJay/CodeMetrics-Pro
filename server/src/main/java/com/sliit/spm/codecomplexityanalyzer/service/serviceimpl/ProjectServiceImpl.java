package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.model.Analysis;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.repository.AnalysisRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl {
//    private final ProjectRepo projectRepo;
//
    @Autowired
    AnalysisRepo analysisRepo;


    public Project save(Project project) {

        Analysis analysis = new Analysis();
        analysis.setCreatedTime(Instant.now().getEpochSecond());
        analysis.setProject(project);
        analysis.setProjectKey(project.getProjectKey());
        analysisRepo.save(analysis);

        return null;
    }

    public Optional<Project> getByKey(String projectKey) {
        return null;
    }

    public List<Project> getAll() {
        return null;
    }

    public List<Analysis> getHistoryByKey(String key) {
//        return analysisRepo.findFirst10ByOrderByCreatedTimeDesc(key);
//        List<Analysis> li = analysisRepo.findAll();
//        if(li.size() > 0) {
//            List<Analysis> al = new ArrayList<>();
//            for(Analysis a : li) {
//                if(a.getProjectKey().equals(key)) {
//                    al.add(a);
//                }
//            }
//            return al;
//        }
        return null;
}

}
