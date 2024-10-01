package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class AdapterDetector {

    public boolean isAdapter(ClassOrInterfaceDeclaration clazz) {
        // Check if class implements two or more interfaces (adapter behavior)
        return clazz.getImplementedTypes().size() >= 2;
    }
}
