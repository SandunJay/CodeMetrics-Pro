package com.sliit.spm.codecomplexityanalyzer.service.pattern;

public class AdapterDetector {

    public boolean isAdapter(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        return interfaces.length >= 2; // Adapts between multiple interfaces
    }
}
