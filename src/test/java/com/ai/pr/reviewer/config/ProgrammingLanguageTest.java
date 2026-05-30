package com.ai.pr.reviewer.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgrammingLanguageTest {

    @Test
    void testFromFileName_Java() {
        assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromFileName("Test.java"));
        assertEquals(ProgrammingLanguage.JAVA, ProgrammingLanguage.fromFileName("src/main/Test.java"));
    }

    @Test
    void testFromFileName_Python() {
        assertEquals(ProgrammingLanguage.PYTHON, ProgrammingLanguage.fromFileName("test.py"));
    }

    @Test
    void testFromFileName_JavaScript() {
        assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromFileName("test.js"));
        assertEquals(ProgrammingLanguage.JAVASCRIPT, ProgrammingLanguage.fromFileName("test.jsx"));
    }

    @Test
    void testFromFileName_TypeScript() {
        assertEquals(ProgrammingLanguage.TYPESCRIPT, ProgrammingLanguage.fromFileName("test.ts"));
        assertEquals(ProgrammingLanguage.TYPESCRIPT, ProgrammingLanguage.fromFileName("test.tsx"));
    }

    @Test
    void testFromFileName_Go() {
        assertEquals(ProgrammingLanguage.GO, ProgrammingLanguage.fromFileName("test.go"));
    }

    @Test
    void testFromFileName_Unknown() {
        assertEquals(ProgrammingLanguage.UNKNOWN, ProgrammingLanguage.fromFileName("test.txt"));
        assertEquals(ProgrammingLanguage.UNKNOWN, ProgrammingLanguage.fromFileName("test.md"));
    }

    @Test
    void testGetDisplayName() {
        assertEquals("Java", ProgrammingLanguage.JAVA.getDisplayName());
        assertEquals("Python", ProgrammingLanguage.PYTHON.getDisplayName());
    }
}
