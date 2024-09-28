package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.service.pattern.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class DesignPatternDetectionService {
    public Map<String, Boolean> detectPatterns(Class<?> clazz) {
        Map<String, Boolean> detectionResults = new HashMap<>();

        detectionResults.put("Singleton", new SingletonDetector().isSingleton(clazz));
        detectionResults.put("Factory Method", new FactoryMethodDetector().isFactoryMethod(clazz));
        detectionResults.put("Adapter", new AdapterDetector().isAdapter(clazz));
        detectionResults.put("Composite", new CompositeDetector().isComposite(clazz));
        detectionResults.put("Decorator", new DecoratorDetector().isDecorator(clazz));
        detectionResults.put("Chain of Responsibility", new ChainOfResponsibilityDetector().isChainOfResponsibility(clazz));
        detectionResults.put("Observer", new ObserverDetector().isObserver(clazz));
        detectionResults.put("Strategy", new StrategyDetector().isStrategy(clazz));

        // Add other patterns similarly...
        return detectionResults;
    }

    public Map<String, Map<String, Boolean>> detectPatternsFromDirectory(String directoryPath) throws IOException, ClassNotFoundException {
        Map<String, Map<String, Boolean>> result = new HashMap<>();

        Files.walk(Paths.get(directoryPath))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        // Convert each file to a Class object or use parsing logic here
                        Class<?> clazz = loadClassFromFile(path);
                        Map<String, Boolean> patterns = detectPatterns(clazz);
                        result.put(path.toString(), patterns);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        return result;
    }

    private Class<?> loadClassFromFile(Path path) throws ClassNotFoundException {
        // Logic to load class from file (this is a placeholder)
        // You could use a library like JavaParser for parsing Java files
        return null;
    }
}
