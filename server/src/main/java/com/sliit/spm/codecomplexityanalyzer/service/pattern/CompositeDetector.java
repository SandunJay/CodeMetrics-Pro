package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Field;

public class CompositeDetector {

    public boolean isComposite(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(clazz)) {
                return true; // Maintains a list of the same type (composite)
            }
        }
        return false;
    }
}
