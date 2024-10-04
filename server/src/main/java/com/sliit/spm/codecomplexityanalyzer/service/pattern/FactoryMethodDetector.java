package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FactoryMethodDetector {

    public boolean isFactoryMethod(ClassOrInterfaceDeclaration clazz) {
        MethodDeclaration[] methods = clazz.getMethods().toArray(new MethodDeclaration[0]);
        for (MethodDeclaration method : methods) {
            if (method.isStatic() && method.getType().isClassOrInterfaceType()) {
                // Check if the return type is an interface
                String returnType = method.getType().asString();
                if (clazz.getImplementedTypes().stream().anyMatch(type -> type.getNameAsString().equals(returnType))) {
                    return true; // Likely a factory method
                }
            }
        }
        return false;
    }
}
