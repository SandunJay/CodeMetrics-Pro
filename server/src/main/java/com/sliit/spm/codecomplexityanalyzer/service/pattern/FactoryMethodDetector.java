package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FactoryMethodDetector {

    public boolean isFactoryMethod(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().isInterface()) {
                return true; // Likely a factory method
            }
        }
        return false;
    }
}
