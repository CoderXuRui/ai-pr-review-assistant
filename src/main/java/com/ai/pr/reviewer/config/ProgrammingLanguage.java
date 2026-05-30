package com.ai.pr.reviewer.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 编程语言枚举
 */
public enum ProgrammingLanguage {
    JAVA("Java", Set.of(".java")),
    PYTHON("Python", Set.of(".py")),
    JAVASCRIPT("JavaScript", Set.of(".js", ".jsx")),
    TYPESCRIPT("TypeScript", Set.of(".ts", ".tsx")),
    GO("Go", Set.of(".go")),
    RUST("Rust", Set.of(".rs")),
    KOTLIN("Kotlin", Set.of(".kt", ".kts")),
    UNKNOWN("Unknown", Set.of());

    private final String displayName;
    private final Set<String> extensions;

    ProgrammingLanguage(String displayName, Set<String> extensions) {
        this.displayName = displayName;
        this.extensions = extensions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getExtensions() {
        return extensions;
    }

    public static ProgrammingLanguage fromFileName(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        return Arrays.stream(values())
            .filter(lang -> lang != UNKNOWN)
            .filter(lang -> lang.getExtensions().stream()
                .anyMatch(lowerFileName::endsWith))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
