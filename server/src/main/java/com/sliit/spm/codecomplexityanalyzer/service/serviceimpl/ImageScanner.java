package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImageScanner {
    public String readImage(String path) {
        ITesseract image  = new Tesseract();
        image.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");
        image.setLanguage("eng");
        String data = "";
        try {
            data = image.doOCR(new File(path));
            System.out.println(data);
        }catch (TesseractException e){
            e.printStackTrace();
        }
        return data;
    }
}
