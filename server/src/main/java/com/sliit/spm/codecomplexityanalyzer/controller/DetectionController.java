package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.AiAnalysisResponse;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.repository.ProjectRepository;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.DesignPatternDetectionService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.FileHandler;
//import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.GeminiApiService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.GroqApiService;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.ImageScanner;
import com.sliit.spm.codecomplexityanalyzer.service.serviceimpl.LanguageDetector;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/detect")
public class DetectionController {
    @Autowired
    private LanguageDetector languageDetector;
    private final ImageScanner imageScanner;
    private final FileHandler fileHandlerService;
    @Autowired
    private DesignPatternDetectionService detectionService;
    @Autowired
    private GroqApiService groqApiService;
    @Autowired
    private ProjectRepository projectRepository;

    public DetectionController(ImageScanner imageScanner, FileHandler fileHandlerService
    ) {
        this.fileHandlerService = fileHandlerService;
        this.imageScanner = imageScanner;
    }

//    Controller to pick Images as multipart files and process it
//@PostMapping("/image")
//public ResponseEntity<?> detectImages(@RequestParam("image") MultipartFile image, @RequestParam("projectId") String projectId) throws Exception {
//    if (image.isEmpty()) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
//    }
//    if (projectId.isEmpty()) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
//    }
//    Project project1 = projectRepository.findById(projectId)
//            .orElseThrow(() -> new RuntimeException("Project not found"));
//
//    if (project1.getCp() != 0) {
//        throw new Exception("Project already has data");
//    }
//    Project project = new Project();
//    project.setProjectKey("ACC");
//    project.setId(projectId);
//    try {
//        String text = imageScanner.readImage(project, image);
//        String detectedLanguage = languageDetector.detectLanguage(text);
//        project.setLanguage(detectedLanguage);
//        Map<String, Boolean> patterns = detectionService.detectPatterns(text, project);
//        Set<String> detectedPatterns = patterns.entrySet().stream()
//                .filter(Map.Entry::getValue)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toSet());
//        project.setPatterns(detectedPatterns);
//        project.setSourcePath(createTempFile(text));
//        fileHandlerService.readFiles(project);
////        AiAnalysisResponse analysisResponse = groqApiService.analyzeCode(text);
////        project.setResponse(analysisResponse);
//        return ResponseEntity.ok(project);
//    } catch (IOException e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error extracting text: " + e.getMessage());
//    }
//}

@PostMapping("/image")
public ResponseEntity<?> detectImages(@RequestParam("image") MultipartFile image, @RequestParam("projectId") String projectId) throws Exception {
    if (image.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
    }
    if (projectId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
    }
    Project project1 = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));

    if (project1.getCp() != 0) {
        throw new Exception("Project already has data");
    }
    Project project = new Project();
    project.setProjectKey("ACC");
    project.setId(projectId);

    String tempFolderPath = null; // Store temp folder path
    try {
        String text = imageScanner.readImage(project, image);
        String detectedLanguage = languageDetector.detectLanguage(text);
        project.setLanguage(detectedLanguage);
        Map<String, Boolean> patterns = detectionService.detectPatterns(text, project);
        Set<String> detectedPatterns = patterns.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        project.setPatterns(detectedPatterns);
        tempFolderPath = createTempFile(text); // Get temp folder path
        project.setSourcePath(tempFolderPath);
        fileHandlerService.readFiles(project);

        return ResponseEntity.ok(project);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error extracting text: " + e.getMessage());
    } finally {
        if (tempFolderPath != null) {
            deleteDirectory(new File(tempFolderPath)); // Delete temp folder after processing
        }
    }
}

    private String createTempFile(String text) throws IOException {
        File tempFile = null;
        Path tempDir = Paths.get("D:/tempPdfText");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }

        int randomNumber = new Random().nextInt(1000);
        Path randomFolder = tempDir.resolve(String.valueOf(randomNumber));
        Files.createDirectories(randomFolder);
        tempFile = Files.createTempFile(randomFolder, "extractedText", ".java").toFile();
        Files.write(tempFile.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return randomFolder.toString();
    }

//    @PostMapping("/pdf")
//    public ResponseEntity<?> detectPdf(@RequestParam("pdf") MultipartFile pdf, @RequestParam("projectId") String projectId) throws Exception {
//        if (pdf.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
//        }
//        if (projectId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
//        }
//        Project project1 = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        if (project1.getCp() != 0) {
//            throw new Exception("Project already has data");
//        }
//        Project project = new Project();
//        project.setId(projectId);
//        project.setProjectKey("ACC");
//        try {
//            String codeText = extractTextFromPdf(pdf);
//            String detectedLanguage = languageDetector.detectLanguage(codeText);
//            project.setLanguage(detectedLanguage);
//            Map<String, Boolean> patterns = detectionService.detectPatterns(codeText, project);
//            Set<String> detectedPatterns = patterns.entrySet().stream()
//                    .filter(Map.Entry::getValue)
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.toSet());
//            project.setPatterns(detectedPatterns);
//            project.setSourcePath(createTempFile(codeText));
//            fileHandlerService.readFiles(project);
////            AiAnalysisResponse analysisResponse = groqApiService.analyzeCode(codeText);
////            project.setResponse(analysisResponse);
//            return ResponseEntity.ok(project);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis: " + e.getMessage());
//        }
//    }

    @PostMapping("/pdf")
    public ResponseEntity<?> detectPdf(@RequestParam("pdf") MultipartFile pdf, @RequestParam("projectId") String projectId) throws Exception {
        if (pdf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
        }
        if (projectId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
        }
        Project project1 = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project1.getCp() != 0) {
            throw new Exception("Project already has data");
        }
        Project project = new Project();
        project.setId(projectId);
        project.setProjectKey("ACC");

        String tempFolderPath = null; // Store temp folder path
        try {
            String codeText = extractTextFromPdf(pdf);
            String detectedLanguage = languageDetector.detectLanguage(codeText);
            project.setLanguage(detectedLanguage);
            Map<String, Boolean> patterns = detectionService.detectPatterns(codeText, project);
            Set<String> detectedPatterns = patterns.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            project.setPatterns(detectedPatterns);
            tempFolderPath = createTempFile(codeText); // Get temp folder path
            project.setSourcePath(tempFolderPath);
            fileHandlerService.readFiles(project);

            return ResponseEntity.ok(project);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis: " + e.getMessage());
        } finally {
            if (tempFolderPath != null) {
                deleteDirectory(new File(tempFolderPath)); // Delete temp folder after processing
            }
        }
    }


    private String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

//    @PostMapping("/text")
//    public ResponseEntity<?> detectText(@RequestBody String text, @RequestParam("projectId") String projectId) throws Exception {
//        if (text.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input text cannot be null or empty.");
//        }
//        if (projectId.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
//        }
//         Project project1 = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        System.out.println(project1);
//        if (project1.getCp() != 0) {
//            throw new Exception("Project already has data");
//        }
//
//        Project project = new Project();
//        project.setId(projectId);
//        project.setProjectKey("ACC");
//        try {
//            String detectedLanguage = languageDetector.detectLanguage(text);
//            project.setLanguage(detectedLanguage);
//            Map<String, Boolean> patterns = detectionService.detectPatterns(text, project);
//            Set<String> detectedPatterns = patterns.entrySet().stream()
//                    .filter(Map.Entry::getValue)
//                    .map(Map.Entry::getKey)
//                    .collect(Collectors.toSet());
//            project.setPatterns(detectedPatterns);
//            project.setSourcePath(createTempTextFile(text));
//            fileHandlerService.readFiles(project);
////            AiAnalysisResponse analysisResponse = groqApiService.analyzeCode(codeText);
////            project.setResponse(analysisResponse);
//            return ResponseEntity.ok(project);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis: " + e.getMessage());
//        }
//    }

    @PostMapping("/text")
    public ResponseEntity<?> detectText(@RequestBody String text, @RequestParam("projectId") String projectId) throws Exception {
        if (text.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Input text cannot be null or empty.");
        }
        if (projectId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
        }
        Project project1 = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project1.getCp() != 0) {
            throw new Exception("Project already has data");
        }

        Project project = new Project();
        project.setId(projectId);
        project.setProjectKey("ACC");

        String tempFolderPath = null;
        try {
            String detectedLanguage = languageDetector.detectLanguage(text);
            project.setLanguage(detectedLanguage);
            Map<String, Boolean> patterns = detectionService.detectPatterns(text, project);
            Set<String> detectedPatterns = patterns.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            project.setPatterns(detectedPatterns);
            tempFolderPath = createTempTextFile(text);
            project.setSourcePath(tempFolderPath);
            fileHandlerService.readFiles(project);

            return ResponseEntity.ok(project);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing text: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis: " + e.getMessage());
        } finally {
            if (tempFolderPath != null) {
                deleteDirectory(new File(tempFolderPath));
            }
        }
    }


    private String createTempTextFile(String text) throws IOException {
        Path tempDir = Paths.get("D:/tempPdfText");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir); // Create the directory if it doesn't exist
        }

        // Generate a random folder inside tempDir to store the file
        int randomNumber = new Random().nextInt(1000);
        Path randomFolder = tempDir.resolve(String.valueOf(randomNumber));
        Files.createDirectories(randomFolder);

        // Create a temporary file inside the random folder
        File tempFile = Files.createTempFile(randomFolder, "extractedText", ".java").toFile();

        // Replace escaped newlines with actual newlines
        String formattedText = text.replace("\\n", "\n");

        // Write the text to the file, preserving the newline characters
        Files.write(tempFile.toPath(), formattedText.getBytes(StandardCharsets.UTF_8));

        // Return the path of the folder where the file was saved
        return randomFolder.toString();
    }

    @PostMapping("/zip")
    public ResponseEntity<?> analyze(@RequestParam("zipFile") MultipartFile zipFile, @RequestParam("projectId") String projectId) throws Exception {
        if (zipFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
        }
        if (projectId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
        }
        Project project1 = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project1.getCp() != 0) {
            throw new Exception("Project already has data");
        }
        Project project = new Project();
        project.setId(projectId);
        project.setProjectKey("ACC");
        File destDir = new File("D:/unzipped");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try {
            unzipFile(zipFile, destDir);
            project.setSourcePath(destDir.getAbsolutePath());
            languageDetector.analyzeProjects(project);
            List<String> codeFilesContent = getCodeFilesContent(destDir);

            Map<String, Boolean> combinedPatterns = new HashMap<>();
            for (String fileContent : codeFilesContent) {
                Map<String, Boolean> patterns = detectionService.detectPatterns(fileContent, project);
                combinedPatterns.putAll(patterns);
            }

            fileHandlerService.readFiles(project);

            return ResponseEntity.ok(project);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing zip file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during pattern detection: " + e.getMessage());
        } finally {
            if (destDir != null) {
                deleteDirectory(destDir);
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

}
