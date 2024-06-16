package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class allinoneTest {
    private static final String INPUT_FILE = "src/main/java/org/example/input.txt";
    private static final String OUTPUT_FILE = "src/main/java/org/example/output.txt";

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(INPUT_FILE));
        Files.deleteIfExists(Paths.get(OUTPUT_FILE));
    }

    @Test
    void testReadAndWriteGraph_MultipleWords() throws IOException {
        Files.write(Paths.get(INPUT_FILE), "hello world\nhello again world".getBytes());
        allinone.readAndWriteGraph();

        List<String> outputLines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(outputLines.contains("hello world 1"));
        assertTrue(outputLines.contains("hello again 1"));
        assertTrue(outputLines.contains("again world 1"));
    }

    @Test
    void testReadAndWriteGraph_EmptyFile() throws IOException {
        Files.createFile(Paths.get(INPUT_FILE));
        allinone.readAndWriteGraph();

        List<String> outputLines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(outputLines.isEmpty());
    }

    @Test
    void testReadAndWriteGraph_SingleWord() throws IOException {
        Files.write(Paths.get(INPUT_FILE), "hello".getBytes());
        allinone.readAndWriteGraph();

        List<String> outputLines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(outputLines.isEmpty());
    }

    @Test
    void testReadAndWriteGraph_RepeatedWords() throws IOException {
        Files.write(Paths.get(INPUT_FILE), "hello hello hello".getBytes());
        allinone.readAndWriteGraph();

        List<String> outputLines = Files.readAllLines(Paths.get(OUTPUT_FILE));
        assertTrue(outputLines.contains("hello hello 2"));
    }

    @Test
    void testReadAndWriteGraph_FileNotFound() {
        assertThrows(IOException.class, () -> {
            allinone.readAndWriteGraph();
        });
    }

}