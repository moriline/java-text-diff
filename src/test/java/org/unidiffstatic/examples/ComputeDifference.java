package org.unidiffstatic.examples;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.UniDiffStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code ComputeDifference} example using String-based
 * {@code diffStatic()} method.
 */
public class ComputeDifference {

    public static final String MOCK_FOLDER = "build/resources/test/mocks/";

    public static String fileToText(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    /**
     * Main example: compute and display differences between two files.
     */
    @Test
    public void main() throws IOException {
        String original = fileToText(MOCK_FOLDER + "original.txt");
        String revised = fileToText(MOCK_FOLDER + "revised.txt");

        // Using String-based diffStatic — produces unified diff output
        String diff = UniDiffStatic.diff(original, revised, "original.txt", "revised.txt", 3);
        System.out.println("=== Unified Diff (first 500 chars) ===");
        System.out.println(diff.substring(0, Math.min(500, diff.length())));

        // Verify round-trip
        try {
            String patched = UniDiffStatic.patch(original, diff);
            String[] origLines = original.split("\n", -1);
            String[] revLines = revised.split("\n", -1);
            String[] patchLines = patched.split("\n", -1);

            if (origLines.length > 0 && origLines[origLines.length - 1].isEmpty()) {
                origLines = java.util.Arrays.copyOf(origLines, origLines.length - 1);
            }
            if (revLines.length > 0 && revLines[revLines.length - 1].isEmpty()) {
                revLines = java.util.Arrays.copyOf(revLines, revLines.length - 1);
            }
            if (patchLines.length > 0 && patchLines[patchLines.length - 1].isEmpty()) {
                patchLines = java.util.Arrays.copyOf(patchLines, patchLines.length - 1);
            }

            assertEquals(revLines.length, patchLines.length);
            for (int i = 0; i < revLines.length; i++) {
                assertEquals(revLines[i], patchLines[i]);
            }
            System.out.println("Round-trip verified successfully!");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Verify that the diff is non-empty and parseable.
     */
    @Test
    public void testDiffIsNonEmpty() throws IOException {
        String original = fileToText(MOCK_FOLDER + "original.txt");
        String revised = fileToText(MOCK_FOLDER + "revised.txt");

        String diff = UniDiffStatic.diff(original, revised, "original.txt", "revised.txt", 3);
        assertFalse(diff.isEmpty(), "Diff should be non-empty for different files");
        assertTrue(diff.contains("--- original.txt"));
        assertTrue(diff.contains("+++ revised.txt"));
        assertTrue(diff.contains("@@"));
    }
}
