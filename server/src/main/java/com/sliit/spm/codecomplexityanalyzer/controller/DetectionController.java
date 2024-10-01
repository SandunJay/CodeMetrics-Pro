package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.GeminiAnalysisResponse;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.DesignPatternDetectionService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.FileHandler;
//import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.GeminiApiService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.GroqApiService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ImageScanner;
import com.sliit.spm.codecomplexityanalyzer.utils.ErrorMessages;
import com.sliit.spm.codecomplexityanalyzer.utils.LanguageDetector;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api/v1/detect")
public class DetectionController {
    @Autowired
    private LanguageDetector languageDetector;
    private final ImageScanner imageScanner;
    private final FileHandler fileHandlerService;
    @Autowired
    private DesignPatternDetectionService detectionService;
//    @Autowired
//    private GeminiApiService geminiApiService;
    @Autowired
    private GroqApiService groqApiService;

    public DetectionController(ImageScanner imageScanner, FileHandler fileHandlerService
    ) {
        this.fileHandlerService = fileHandlerService;
        this.imageScanner = imageScanner;
    }

//    @GetMapping("/analyze")
//    public ResponseEntity<List<Project>> analyzeProjects(@RequestParam String folderPath) {
//        List<Project> projectInfoList = languageDetector.analyzeProjects(folderPath);
//        if (projectInfoList.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(projectInfoList);
//    }


//    Controller to pick Images as multipart files and process it
@PostMapping("/image")
public ResponseEntity<?> detectImages(@RequestParam("image") MultipartFile image) {
    Project project = new Project();
    project.setProjectKey("ACC");
    if (image.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
    }
    try {
        String text = imageScanner.readImage(project, image);
        Map<String, Boolean> patterns = detectionService.detectPatterns(text, project);
        return ResponseEntity.ok(patterns);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error extracting text: " + e.getMessage());
    }
}

    @PostMapping("/pdf")
    public ResponseEntity<?> detectPdf(@RequestParam("pdf") MultipartFile pdf) throws Exception {
        Project project = new Project();
        project.setProjectKey("ACC");

        if (pdf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
        }

//        try {
            // Extract text from PDF using PDFBox
            String codeText = extractTextFromPdf(pdf);

            // Detect the programming language
            String detectedLanguage = languageDetector.detectLanguage(codeText);
            project.setLanguage(detectedLanguage);

            // Detect design patterns
            Map<String, Boolean> patterns = detectionService.detectPatterns(codeText, project);

            // Use Gemini API for AI overview, completion, and improvements
            GeminiAnalysisResponse analysis = groqApiService.analyzeCode(codeText);
            String completedCode = groqApiService.getCompletedCode(codeText);
            String improvedCode = groqApiService.getImprovedCode(codeText);

            // Response with detected patterns, AI overview, and improvements
            return ResponseEntity.ok(Map.of(
                    "detectedLanguage", detectedLanguage,
                    "patterns", patterns,
                    "geminiOverview", analysis.getOverview()
//                    "completedCode", completedCode,
//                    "improvedCode", improvedCode
            ));

//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis: " + e.getMessage());
//        }
    }

    // Extract text from the PDF using PDFBox
    private String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

//    To detect a code in Local storage and read its directories and subdirectories
//    @GetMapping("/analyze")
//    public ResponseEntity<List<Project>> analyzeBaseProject(@RequestParam String filePath){
//        List<Project> projectList = languageDetector.analyzeProjects(filePath);
//        if (projectList.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        Project project = new Project();
//        project.setProjectKey("ACC");
//        if (!filePath.isEmpty()) {
//            project.setSourcePath(filePath);
//        } else {
//            throw new NoSuchElementException(ErrorMessages.SP_NOT_FOUND_ERR);
//        }
//        fileHandlerService.readFiles(project);
//        return null;
//    }

    @PostMapping("/zip")
    public ResponseEntity<?> analyze(@RequestParam("zipFile") MultipartFile zipFile) {
        Project project = new Project();
        project.setProjectKey("ACC");
        if (zipFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
        }
        File tempDir = null;
        try {
            tempDir = Files.createTempDirectory("unzipped").toFile();
            unzipFile(zipFile, tempDir);
            project.setSourcePath(tempDir.getAbsolutePath());
            languageDetector.analyzeProjects(project);
            List<String> codeFilesContent = getCodeFilesContent(tempDir);

            Map<String, Boolean> combinedPatterns = new HashMap<>();
            for (String fileContent : codeFilesContent) {
                Map<String, Boolean> patterns = detectionService.detectPatterns(fileContent, project);
                combinedPatterns.putAll(patterns);
            }
            return ResponseEntity.ok(project.getPatterns());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing zip file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during pattern detection: " + e.getMessage());
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
    }

//    Utility functions for zipped source code upload
    private void unzipFile(MultipartFile zipFile, File destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
    //    Utility functions for zipped source code upload
    private List<String> getCodeFilesContent(File rootDir) throws IOException {
        List<String> codeFilesContent = new ArrayList<>();
        Files.walk(Paths.get(rootDir.getAbsolutePath()))
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    try {
                        String fileContent = new String(Files.readAllBytes(filePath));
                        codeFilesContent.add(fileContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return codeFilesContent;
    }

    //    Utility functions for zipped source code upload
    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }

//    To get the code from an image in local storage
//    @GetMapping("/extract")
//    public ResponseEntity<String> extractText(@RequestParam String filePath) {
//        Project project = new Project();
//        if (filePath.isEmpty()){
//            throw new  NullPointerException("File Path cannot be null");
//        }
//        try {
//            String decodedPath = URLDecoder.decode(filePath, "UTF-8");
//            project.setSourcePath(decodedPath);
//            String text = imageScanner.readImage(project);
//            return ResponseEntity.ok(text);
//        } catch (UnsupportedEncodingException e) {
//            return ResponseEntity.status(500).body("Error decoding file path: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error extracting text: " + e.getMessage());
//        }
//    }

//  Below methods are generated for detecting design patterns


//    @PostMapping("/detect")
//    public ResponseEntity<Map<String, Boolean>> detectPatterns(@RequestParam("classFile") MultipartFile classFile) throws ClassNotFoundException {
//        // Load the class from the file (this is a placeholder)
//        Class<?> clazz = loadClassFromFile(classFile);
//        Map<String, Boolean> patterns = detectionService.detectPatterns(clazz);
//        return ResponseEntity.ok(patterns);
//    }
//
//    @PostMapping("/detect-from-dir")
//    public ResponseEntity<Map<String, Map<String, Boolean>>> detectPatternsFromDirectory(@RequestParam("directoryPath") String directoryPath) throws ClassNotFoundException, IOException {
//        Map<String, Map<String, Boolean>> results = detectionService.detectPatternsFromDirectory(directoryPath);
//        return ResponseEntity.ok(results);
//    }
//
//    private Class<?> loadClassFromFile(MultipartFile file) throws ClassNotFoundException {
//        // Implement loading logic for class from file
//        return null;
//    }

}
