package com.sliit.spm.codecomplexityanalyzer.service.serviceimpl;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class ImageScannerService {
    public String readImage() {
        ITesseract image  = new Tesseract();
        String data = "";
        try {
            data = image.doOCR(new File("C:\\Users\\sandu\\Downloads\\Report generation sequence diagram.drawio.png"));
        }catch (TesseractException e){
            e.printStackTrace();
        }
        return data;
    }
}
