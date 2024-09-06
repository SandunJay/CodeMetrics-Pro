package com.sliit.spm.codecomplexityanalyzer.utils;

import com.sliit.spm.codecomplexityanalyzer.model.Project;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class LanguageDetector {
    private static final List<String> JAVA_KEYWORDS = Arrays.asList("public", "class", "System.out.println", "import", "extends", "implements", "void", "static", "package", "new", "try", "catch");
    private static final List<String> PYTHON_KEYWORDS = Arrays.asList("def", "import", "print", "self", ":", "lambda", "pass", "elif", "in", "indentation");
    private static final List<String> CPP_KEYWORDS = Arrays.asList("#include", "iostream", "std::", "int main()", "cout", "cin", "vector", "namespace", "new", "delete");
    private static final List<String> JAVASCRIPT_KEYWORDS = Arrays.asList("function", "var", "let", "const", "console.log", "document", "window", "=>", "async", "await");
    private static final List<String> RUBY_KEYWORDS = Arrays.asList("def", "end", "puts", "class", "module", "yield", "do", "if", "else", "begin");
    private static final List<String> GO_KEYWORDS = Arrays.asList("func", "import", "package", "fmt.Println", "defer", "go", "chan", "main", "var");
    private static final List<String> KOTLIN_KEYWORDS = Arrays.asList("fun", "val", "var", "println", "class", "object", "interface", "when", "package", "companion");

    private static final double SIGNIFICANCE_THRESHOLD = 0.2;

    // Set to store unique keywords across all languages
    private static final Set<String> ALL_KEYWORDS = new HashSet<>();

    public LanguageDetector() {
        // Populate ALL_KEYWORDS with keywords from all languages
        ALL_KEYWORDS.addAll(JAVA_KEYWORDS);
        ALL_KEYWORDS.addAll(PYTHON_KEYWORDS);
        ALL_KEYWORDS.addAll(CPP_KEYWORDS);
        ALL_KEYWORDS.addAll(JAVASCRIPT_KEYWORDS);
        ALL_KEYWORDS.addAll(RUBY_KEYWORDS);
        ALL_KEYWORDS.addAll(GO_KEYWORDS);
        ALL_KEYWORDS.addAll(KOTLIN_KEYWORDS);
    }

    // Detects the programming language based on the OCR-processed code
    public String detectLanguage(String code) {
        code = code.toLowerCase();

        double javaScore = calculateUniqueKeywordMatch(code, JAVA_KEYWORDS);
        double pythonScore = calculateUniqueKeywordMatch(code, PYTHON_KEYWORDS);
        double cppScore = calculateUniqueKeywordMatch(code, CPP_KEYWORDS);
        double javascriptScore = calculateUniqueKeywordMatch(code, JAVASCRIPT_KEYWORDS);
        double rubyScore = calculateUniqueKeywordMatch(code, RUBY_KEYWORDS);
        double goScore = calculateUniqueKeywordMatch(code, GO_KEYWORDS);
        double kotlinScore = calculateUniqueKeywordMatch(code, KOTLIN_KEYWORDS);

        // Determine the language with the highest score
        if (javaScore >= SIGNIFICANCE_THRESHOLD) {
            return "Java";
        } else if (pythonScore >= SIGNIFICANCE_THRESHOLD) {
            return "Python";
        } else if (cppScore >= SIGNIFICANCE_THRESHOLD) {
            return "C++";
        } else if (javascriptScore >= SIGNIFICANCE_THRESHOLD) {
            return "JavaScript";
        } else if (rubyScore >= SIGNIFICANCE_THRESHOLD) {
            return "Ruby";
        } else if (goScore >= SIGNIFICANCE_THRESHOLD) {
            return "Go";
        } else if (kotlinScore >= SIGNIFICANCE_THRESHOLD) {
            return "Kotlin";
        } else {
            return "Unknown Language";
        }
    }

    // Calculates the percentage of unique keyword matches for the given language
    private double calculateUniqueKeywordMatch(String code, List<String> languageKeywords) {
        int matchCount = 0;
        int totalUniqueKeywords = 0;

        // Check each keyword and only consider it if it is unique to the language
        for (String keyword : languageKeywords) {
            if (isUniqueKeyword(keyword, languageKeywords)) {
                totalUniqueKeywords++;
                if (code.contains(keyword.toLowerCase())) {
                    matchCount++;
                }
            }
        }

        return totalUniqueKeywords == 0 ? 0 : (double) matchCount / totalUniqueKeywords;
    }

    // Checks if a keyword is unique to a given language
    private boolean isUniqueKeyword(String keyword, List<String> languageKeywords) {
        // A keyword is unique if it appears only in the given language's keyword list
        int countInAllLanguages = 0;
        for (String otherKeyword : ALL_KEYWORDS) {
            if (otherKeyword.equalsIgnoreCase(keyword)) {
                countInAllLanguages++;
            }
        }
        // If the keyword appears more than once across all languages, it's not unique
        return countInAllLanguages == 1;
    }

    public List<Project> analyzeProjects(String folderPath) {
        File rootFolder = new File(folderPath);
        List<Project> projects = new ArrayList<>();

        if (rootFolder.exists() && rootFolder.isDirectory()) {
            File[] subFolders = rootFolder.listFiles(File::isDirectory);
            if (subFolders != null) {
                for (File subFolder : subFolders) {
                    String language = detectLanguage(subFolder);
                    if (language != null) {
                        projects.add(new Project(subFolder.getName(), null,folderPath, language, null, 0));
                    }
                }
            }
        }
        return projects;
    }

    private String detectLanguage(File projectFolder) {
        File[] files = projectFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".java") || fileName.equals("pom.xml") || fileName.equals("build.gradle")) {
                    return "Java";
                } else if (fileName.endsWith(".js") || fileName.endsWith(".ts") || fileName.equals("package.json")) {
                    return "JavaScript/TypeScript";
                } else if (fileName.endsWith(".go") || fileName.equals("go.mod")) {
                    return "Golang";
                } else if (fileName.endsWith(".py") || fileName.equals("requirements.txt")) {
                    return "Python";
                } else if (fileName.endsWith(".cpp") || fileName.endsWith(".h") || fileName.endsWith(".c")) {
                    return "C++";
                } else if (fileName.endsWith(".kt")) {
                    return "Kotlin";
                } else if (fileName.endsWith(".rb") || fileName.equals("Gemfile")) {
                    return "Ruby";
                }
            }
        }
        return null;
    }
}
