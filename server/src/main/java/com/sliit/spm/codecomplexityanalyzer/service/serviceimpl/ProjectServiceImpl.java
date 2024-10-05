package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.dto.AnalyzedDataDto;
import com.sliit.spm.codecomplexityanalyzer.dto.ProjectDto;
import com.sliit.spm.codecomplexityanalyzer.model.User;
import com.sliit.spm.codecomplexityanalyzer.repository.UserRepository;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
//import com.sliit.spm.codecomplexityanalyzer.repository.AnalysisRepo;
import com.sliit.spm.codecomplexityanalyzer.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl {
////    private final ProjectRepo projectRepo;
////
//    @Autowired
//    AnalysisRepo analysisRepo;
//
//
//    public Project save(Project project) {
//
//        Analysis analysis = new Analysis();
//        analysis.setCreatedTime(Instant.now().getEpochSecond());
//        analysis.setProject(project);
//        analysis.setProjectKey(project.getProjectKey());
//        analysisRepo.save(analysis);
//
//        return null;
//    }
//
//    public Optional<Project> getByKey(String projectKey) {
//        return null;
//    }
//
//    public List<Project> getAll() {
//        return null;
//    }
//
//    public List<Analysis> getHistoryByKey(String key) {
////        return analysisRepo.findFirst10ByOrderByCreatedTimeDesc(key);
////        List<Analysis> li = analysisRepo.findAll();
////        if(li.size() > 0) {
////            List<Analysis> al = new ArrayList<>();
////            for(Analysis a : li) {
////                if(a.getProjectKey().equals(key)) {
////                    al.add(a);
////                }
////            }
////            return al;
////        }
//        return null;
//}

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // Create project
    public Project createProject(ProjectDto projectDto) {
        Project project = new Project();
        User user = userRepository.findByEmail(projectDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.setUser(user.getId());
        project.setName(projectDto.getName());
//        project.setProjectKey(projectDto.getProjectKey());

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects(String userId) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return projectRepository.findByUser(user.getId());
    }

    // Update project with analyzed data
    public Project updateProject(String projectId, AnalyzedDataDto analyzedData) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setLanguage(analyzedData.getLanguage());
        project.setPatterns(analyzedData.getPatterns());
        project.setResponse(analyzedData.getAiAnalysisResponse());

        return projectRepository.save(project);
    }

    // Delete project
    public void deleteProject(String projectId) {
        projectRepository.deleteById(projectId);
    }

    // Get project by ID
    public Project getProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}
