package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import com.sliit.spm.codecomplexityanalyzer.utils.LanguageDetector;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImageScanner {

    @Autowired
    private final LanguageDetector languageDetector;

    public ImageScanner(LanguageDetector languageDetector) {
        this.languageDetector = languageDetector;
    }

    public String readImage(Project project) {
        ITesseract image  = new Tesseract();
        image.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        image.setLanguage("eng");
        String data = "";

        try {
            data = image.doOCR(new File(project.getSourcePath()));
            project.setLanguage(languageDetector.detectLanguage(data));
            System.out.println(project.getLanguage());
            System.out.println(data);
        }catch (TesseractException e){
            e.printStackTrace();
        }
        return data;
    }
}
