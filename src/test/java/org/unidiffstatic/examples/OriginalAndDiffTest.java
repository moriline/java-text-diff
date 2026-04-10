package org.unidiffstatic.examples;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code OriginalAndDiffTest} using String-based
 * {@code diffStatic()} method instead of
 * {@code UnifiedDiffUtils.generateOriginalAndDiff()}.
 */
public class OriginalAndDiffTest {

    public static final String MOCK_FOLDER = "build/resources/test/mocks/";

    public static String fileToText(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    /**
     * Generate unified diff for original.txt vs revised.txt.
     */
    @Test
    public void testGenerateOriginalAndDiff() throws IOException {
        String origText = fileToText(MOCK_FOLDER + "original.txt");
        String revText = fileToText(MOCK_FOLDER + "revised.txt");

        String diff = JavaTextDiff.diff(origText, revText, "original.txt", "revised.txt", 10);
        System.out.println("=== Original and Diff ===");
        System.out.println(diff);

        assertFalse(diff.isEmpty());
        assertTrue(diff.contains("--- original.txt"));
        assertTrue(diff.contains("+++ revised.txt"));
    }

    /**
     * Generate unified diff for issue_170 files — tests first line change.
     */
    @Test
    public void testGenerateOriginalAndDiffFirstLineChange() throws IOException {
        String origText = fileToText(MOCK_FOLDER + "issue_170_original.txt");
        String revText = fileToText(MOCK_FOLDER + "issue_170_revised.txt");

        String diff = JavaTextDiff.diff(origText, revText, "issue_170_original.txt", "issue_170_revised.txt", 10);
        System.out.println("=== First Line Change Diff ===");
        System.out.println(diff);

        assertFalse(diff.isEmpty());
        // The first line should be changed, so diff should appear early
        assertTrue(diff.contains("@@"));
    }

    /**
     * Verify round-trip: diffStatic → patchStatic reproduces revised text.
     */
    @Test
    public void testRoundTrip() throws Exception {
        String origText = fileToText(MOCK_FOLDER + "original.txt");
        String revText = fileToText(MOCK_FOLDER + "revised.txt");

        String diff = JavaTextDiff.diff(origText, revText, "original.txt", "revised.txt", 10);
        String patched = JavaTextDiff.patch(origText, diff);

        // Normalize trailing newlines
        String[] revLines = revText.split("\n", -1);
        String[] patchLines = patched.split("\n", -1);
        if (revLines.length > 0 && revLines[revLines.length - 1].isEmpty()) {
            revLines = java.util.Arrays.copyOf(revLines, revLines.length - 1);
        }
        if (patchLines.length > 0 && patchLines[patchLines.length - 1].isEmpty()) {
            patchLines = java.util.Arrays.copyOf(patchLines, patchLines.length - 1);
        }

        assertEquals(revLines.length, patchLines.length, "Number of lines must match");
        for (int i = 0; i < revLines.length; i++) {
            assertEquals(revLines[i], patchLines[i], "Line " + (i + 1) + " must match");
        }
    }
}
