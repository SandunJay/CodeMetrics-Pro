package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Field;

public class DecoratorDetector {

    public boolean isDecorator(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(clazz)) {
                return true; // Holds a reference to the same interface
            }
        }
        return false;
    }
}
