package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.lang.reflect.Field;
import java.util.Collection;

public class ObserverDetector {

    public boolean isObserver(ClassOrInterfaceDeclaration clazz) {
        // Check if class has a collection field (for maintaining a list of observers)
        for (var field : clazz.getFields()) {
            if (field.getElementType().isClassOrInterfaceType() &&
                    field.getElementType().asString().contains("Collection")) {
                return true; // Likely maintains a collection of observers
            }
        }
        return false;
    }
}
