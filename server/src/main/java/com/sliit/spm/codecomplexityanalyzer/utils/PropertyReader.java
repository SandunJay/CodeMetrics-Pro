package com.sliit.spm.codecomplexityanalyzer.utils;



import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyReader {

    
    private static PropertyReader propertyReader = null;

    private Map<String, String> properties = new HashMap<>();

    private PropertyReader() {
        try (FileReader reader = new FileReader("config/application.properties")) {
            Properties p = new Properties();
            p.load(reader);
            p.entrySet().forEach(entry -> properties.put(entry.getKey().toString(), entry.getValue().toString()));
        } catch (IOException e) {
            }
    }

    public static PropertyReader getInstance() {
        if (propertyReader == null)
            propertyReader = new PropertyReader();

        return propertyReader;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }
}
