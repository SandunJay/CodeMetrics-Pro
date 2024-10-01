package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.lang.reflect.Field;

public class StrategyDetector {

    public boolean isStrategy(ClassOrInterfaceDeclaration clazz) {
        if (clazz.isInterface()) {
            return true;
        }

        if (!clazz.getImplementedTypes().isEmpty()) {
            return true;
        }

        for (var field : clazz.getFields()) {
            if (field.getElementType().isClassOrInterfaceType()) {
                ClassOrInterfaceType type = (ClassOrInterfaceType) field.getElementType();
                if (type.isClassOrInterfaceType()) {
                    return true;
                }
            }
        }

        return false;
    }
}
