package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ChainOfResponsibilityDetector {

    public boolean isChainOfResponsibility(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (Arrays.stream(method.getParameterTypes())
                    .anyMatch(param -> param.isAssignableFrom(clazz))) {
                return true; // Passes the request to the next handler
            }
        }
        return false;
    }
}
