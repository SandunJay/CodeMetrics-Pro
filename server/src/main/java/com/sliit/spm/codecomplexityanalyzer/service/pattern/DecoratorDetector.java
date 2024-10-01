package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.lang.reflect.Field;

public class DecoratorDetector {

    public boolean isDecorator(ClassOrInterfaceDeclaration clazz) {
        // Check if class has a field of its own type (decorator behavior)
        for (var field : clazz.getFields()) {
            if (field.getElementType().asString().equals(clazz.getNameAsString())) {
                return true; // Holds a reference to the same interface
            }
        }
        return false;
    }
}
