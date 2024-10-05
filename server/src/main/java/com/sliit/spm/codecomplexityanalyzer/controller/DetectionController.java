package com.sliit.spm.codecomplexityanalyzer.controller;

import com.sliit.spm.codecomplexityanalyzer.model.AiAnalysisResponse;
import com.sliit.spm.codecomplexityanalyzer.model.Project;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public DetectionController(ImageScanner imageScanner, FileHandler fileHandlerService
    ) {
        this.fileHandlerService = fileHandlerService;
        this.imageScanner = imageScanner;
    }

//    Controller to pick Images as multipart files and process it
@PostMapping("/image")
public ResponseEntity<?> detectImages(@RequestParam("image") MultipartFile image, @RequestParam("projectId") String projectId) {
    if (image.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
    }
    if (projectId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
    }
    Project project = new Project();
    project.setProjectKey("ACC");
    project.setId(projectId);
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
//        AiAnalysisResponse analysisResponse = groqApiService.analyzeCode(text);
//        project.setResponse(analysisResponse);

//        return ResponseEntity.ok(Map.of(
//                "detectedLanguage", project.getLanguage(),
//                "patterns", patterns,
//                "geminiOverview", project.getResponse().getOverview(),
//                "completedCode", project.getResponse().getCompletedCode(),
//                "improvedCode", project.getResponse().getImprovedCode()
//        ));

        project.setSourcePath(createTempFile(text));

        fileHandlerService.readFiles(project);
//        AiAnalysisResponse analysisResponse = groqApiService.analyzeCode(text);
//        project.setResponse(analysisResponse);
        return ResponseEntity.ok(project);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image: " + e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error extracting text: " + e.getMessage());
    }
}

    private String createTempFile(String text) throws IOException {
        File tempFile = null;
        // Create a temporary file in the D drive and write the extracted text to it
        Path tempDir = Paths.get("D:/tempPdfText");
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir); // Create the directory if it doesn't exist
        }

        // Generate a random number
        int randomNumber = new Random().nextInt(1000); // adjust the range as needed

        // Create a folder with the random number as its name
        Path randomFolder = tempDir.resolve(String.valueOf(randomNumber));
        Files.createDirectories(randomFolder);

        tempFile = Files.createTempFile(randomFolder, "extractedText", ".java").toFile();
        Files.write(tempFile.toPath(), text.getBytes(StandardCharsets.UTF_8));

        // Set the file path in the project
        return randomFolder.toString();
    }

    @PostMapping("/pdf")
    public ResponseEntity<?> detectPdf(@RequestParam("pdf") MultipartFile pdf, @RequestParam("projectId") String projectId) throws Exception {
        if (pdf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
        }
        if (projectId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
        }
        Project project = new Project();
        project.setId(projectId);
        project.setProjectKey("ACC");
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

//            project.setSourcePath(destDir.getAbsolutePath());

//            return ResponseEntity.ok(Map.of(
//                    "detectedLanguage", detectedLanguage,
//                    "patterns", patterns,
//                    "geminiOverview", project.getResponse().getOverview(),
//                    "completedCode", project.getResponse().getCompletedCode(),
//                    "improvedCode", project.getResponse().getImprovedCode()
//            ));

            project.setSourcePath(createTempFile(codeText));
            fileHandlerService.readFiles(project);
//            AiAnalysisResponse analysisResponse = groqApiService.analyzeCode(codeText);
//            project.setResponse(analysisResponse);
            return ResponseEntity.ok(project);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing PDF: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during analysis: " + e.getMessage());
        }
    }

    private String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    @PostMapping("/zip")
    public ResponseEntity<?> analyze(@RequestParam("zipFile") MultipartFile zipFile, @RequestParam("projectId") String projectId) {
        if (zipFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File cannot be null or empty.");
        }
        if (projectId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ProjectId cannot be null or empty.");
        }
        Project project = new Project();
        project.setId(projectId);
        project.setProjectKey("ACC");
        File destDir = new File("D:/unzipped");
        if (!destDir.exists()) {
            destDir.mkdirs(); // Create the directory if it doesn't exist
        }

//        File tempDir = null;
        try {
//            tempDir = Files.createTempDirectory("unzipped").toFile();
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

    //    @GetMapping("/analyze")
//    public ResponseEntity<List<Project>> analyzeProjects(@RequestParam String folderPath) {
//        List<Project> projectInfoList = languageDetector.analyzeProjects(folderPath);
//        if (projectInfoList.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(projectInfoList);
//    }

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

}
