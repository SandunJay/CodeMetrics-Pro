package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.utils.LanguageDetector;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ImageScanner {

    private final LanguageDetector languageDetector;

    @Autowired
    public ImageScanner(LanguageDetector languageDetector) {
        this.languageDetector = languageDetector;
    }

    public String readImage(Project project, MultipartFile file) throws IOException {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        tesseract.setLanguage("eng");

        // Convert MultipartFile to a temporary file
        File tempFile = File.createTempFile("image", ".png");
        file.transferTo(tempFile);

        String extractedText = "";

        try {
            extractedText = tesseract.doOCR(tempFile);
            project.setLanguage(languageDetector.detectLanguage(extractedText));
            System.out.println(project.getLanguage());
            System.out.println(extractedText);
        } catch (TesseractException e) {
            e.printStackTrace();
        } finally {
            // Clean up the temporary file
            tempFile.delete();
        }

        return extractedText;
    }
}

