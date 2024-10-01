package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ChainOfResponsibilityDetector {

    public boolean isChainOfResponsibility(ClassOrInterfaceDeclaration clazz) {
        // Check if methods pass the request to another object of the same type (chain of responsibility)
        for (var method : clazz.getMethods()) {
            for (var param : method.getParameters()) {
                if (param.getType().asString().equals(clazz.getNameAsString())) {
                    return true; // Passes the request to the next handler
                }
            }
        }
        return false;
    }
}
