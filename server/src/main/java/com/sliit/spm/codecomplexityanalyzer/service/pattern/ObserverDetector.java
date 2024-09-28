package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Field;
import java.util.Collection;

public class ObserverDetector {

    public boolean isObserver(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                return true; // Likely maintains a collection of observers
            }
        }
        return false;
    }
}
