package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.lang.reflect.Field;

public class CompositeDetector {

    public boolean isComposite(ClassOrInterfaceDeclaration clazz) {
        // Check if class has fields that are the same type as itself (composite behavior)
        for (var field : clazz.getFields()) {
            if (field.getElementType().asString().equals(clazz.getNameAsString())) {
                return true; // Maintains a list of the same type (composite)
            }
        }
        return false;
    }
}
