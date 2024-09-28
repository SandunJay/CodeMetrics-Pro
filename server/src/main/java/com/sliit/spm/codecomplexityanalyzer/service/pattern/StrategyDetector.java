package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Field;

public class StrategyDetector {

    public boolean isStrategy(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().isInterface()) {
                return true; // Delegates to a strategy interface
            }
        }
        return false;
    }
}
