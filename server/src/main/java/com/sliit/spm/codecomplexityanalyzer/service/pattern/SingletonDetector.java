package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class SingletonDetector {

    public boolean isSingleton(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        boolean hasPrivateConstructor = Arrays.stream(constructors)
                .anyMatch(constructor -> Modifier.isPrivate(constructor.getModifiers()));

        boolean hasStaticInstanceMethod = Arrays.stream(clazz.getMethods())
                .anyMatch(method -> Modifier.isStatic(method.getModifiers()) && method.getName().equals("getInstance"));

        return hasPrivateConstructor && hasStaticInstanceMethod;
    }
}
