package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.service.pattern.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class DesignPatternDetectionService {
    public Map<String, Boolean> detectPatterns(String code, Project project) throws Exception {
        Map<String, Boolean> detectionResults = new HashMap<>();

        switch (project.getLanguage().toLowerCase()) {
            case "java":
                detectionResults.putAll(detectJavaPatterns(code, project));
                break;
            case "python":
                detectionResults.putAll(detectPythonPatterns(code, project));
                break;
            case "c++":
                detectionResults.putAll(detectCppPatterns(code, project));
                break;
            case "golang":
                detectionResults.putAll(detectGoPatterns(code, project));
                break;
            default:
                detectionResults.put("Unknown Language", false);
        }
        return detectionResults;
    }


    public Map<String, Boolean> detectJavaPatterns(String code, Project project) throws Exception {
        Map<String, Boolean> detectionResults = new HashMap<>();
        JavaParser parser = new JavaParser();

        try {
            Optional<CompilationUnit> cu = parser.parse(code).getResult();

            cu.ifPresent(unit -> {
                List<PatternDetector> detectors = List.of(
                        new PatternDetector("Singleton", new SingletonDetector()::isSingleton),
                        new PatternDetector("Factory Method", new FactoryMethodDetector()::isFactoryMethod),
                        new PatternDetector("Adapter", new AdapterDetector()::isAdapter),
                        new PatternDetector("Strategy", new StrategyDetector()::isStrategy),
                        new PatternDetector("Observer", new ObserverDetector()::isObserver),
                        new PatternDetector("Composite", new CompositeDetector()::isComposite),
                        new PatternDetector("Decorator", new DecoratorDetector()::isDecorator),
                        new PatternDetector("Chain of Responsibility", new ChainOfResponsibilityDetector()::isChainOfResponsibility)
                );

                unit.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz ->
                        detectors.forEach(detector -> {
                            boolean detected = detector.getDetector().apply(clazz);
                            detectionResults.put(detector.getPatternName(), detectionResults.getOrDefault(detector.getPatternName(), false) || detected);
                            if (detected) project.getPatterns().add(detector.getPatternName());
                        })
                );
            });
        } catch (Exception e) {
            throw new Exception("Error processing design pattern detection: " + e.getMessage());
        }

        return detectionResults;
    }


    private Map<String, Boolean> detectPythonPatterns(String code, Project project) {
        return new HashMap<>();
    }

    private Map<String, Boolean> detectCppPatterns(String code, Project project) {
        return new HashMap<>();
    }

    private Map<String, Boolean> detectGoPatterns(String code, Project project) {
        return new HashMap<>();
    }

}

 class PatternDetector {
    private String patternName;
    private Function<ClassOrInterfaceDeclaration, Boolean> detector;

    public PatternDetector(String patternName, Function<ClassOrInterfaceDeclaration, Boolean> detector) {
        this.patternName = patternName;
        this.detector = detector;
    }

    public String getPatternName() {
        return patternName;
    }

    public Function<ClassOrInterfaceDeclaration, Boolean> getDetector() {
        return detector;
    }
}

