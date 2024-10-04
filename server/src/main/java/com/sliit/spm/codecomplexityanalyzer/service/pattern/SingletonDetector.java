package com.sliit.spm.codecomplexityanalyzer.service.pattern;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class SingletonDetector {

    public boolean isSingleton(ClassOrInterfaceDeclaration classDeclaration) {
        boolean hasPrivateConstructor = classDeclaration.getConstructors().stream()
                .anyMatch(constructor -> constructor.isPrivate());

        boolean hasStaticFieldOfType = classDeclaration.getFields().stream()
                .anyMatch(field -> field.isStatic()
                        && field.getElementType().asString().equals(classDeclaration.getNameAsString()));

        boolean hasGetInstanceMethod = classDeclaration.getMethods().stream()
                .anyMatch(method -> method.isStatic()
                        && method.getNameAsString().equals("getInstance")
                        && method.getType().asString().equals(classDeclaration.getNameAsString())
                        && method.getParameters().isEmpty());

        boolean hasEagerInitialization = classDeclaration.getFields().stream()
                .anyMatch(field -> field.isStatic() && field.isFinal() &&
                        field.getElementType().asString().equals(classDeclaration.getNameAsString()));

        boolean isEnum = classDeclaration.isEnumDeclaration();

        return hasPrivateConstructor && (hasStaticFieldOfType || hasEagerInitialization || isEnum)
                && hasGetInstanceMethod;
    }
}
