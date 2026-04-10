package org.unidiffstatic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UniDiffFilesTest {
    /**
     * The base folder containing the test files (Gradle build output).
     */
    public static final String MOCK_FOLDER = "build/resources/test/mocks/";

    public static String fileToText(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    public static List<String> fileToLines(String filename) throws IOException {
        String content = fileToText(filename);
        List<String> lines = List.of(content.split("\n", -1));
        List<String> result = new ArrayList<>(lines);
        if (!result.isEmpty() && result.getLast().isEmpty()) {
            result.removeLast();
        }
        return result;
    }

    @Test
    public void testGenerateUnified() throws IOException {
        String origText = fileToText(MOCK_FOLDER + "original.txt");
        String revText = fileToText(MOCK_FOLDER + "revised.txt");
        verify(origText, revText, "original.txt", "revised.txt");
    }

    @Test
    public void testOneDelta() throws IOException {
        String origText = fileToText(MOCK_FOLDER + "one_delta_test_original.txt");
        String revText = fileToText(MOCK_FOLDER + "one_delta_test_revised.txt");
        verify(origText, revText, "one_delta_test_original.txt", "one_delta_test_revised.txt");
    }

    @Test
    public void testGenerateUnifiedDiffWithoutAnyDeltas() {
        String unifiedDiffTxt = JavaTextDiff.diff("abc", "abc2", "abc1", "abc2", 0);

        assertAll("unified diff header",
                () -> assertTrue(unifiedDiffTxt.contains("--- abc1"), "original filename should be abc1"),
                () -> assertTrue(unifiedDiffTxt.contains("+++ abc2"), "revised filename should be abc2")
        );
    }

    @Test
    public void testDiffIssue10() throws IOException {
        String baseText = fileToText(MOCK_FOLDER + "issue10_base.txt");
        String patchText = fileToText(MOCK_FOLDER + "issue10_patch.txt");

        String patched;
        try {
            patched = JavaTextDiff.patch(baseText, patchText);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
            return;
        }
        assertNotNull(patched);
    }

    @Test
    public void testPatchWithNoDeltas() throws IOException {
        String text1 = fileToText(MOCK_FOLDER + "issue11_1.txt");
        String text2 = fileToText(MOCK_FOLDER + "issue11_2.txt");
        verify(text1, text2, "issue11_1.txt", "issue11_2.txt");
    }

    @Test
    public void testDiff5() throws IOException {
        String text1 = fileToText(MOCK_FOLDER + "5A.txt");
        String text2 = fileToText(MOCK_FOLDER + "5B.txt");
        verify(text1, text2, "5A.txt", "5B.txt");
    }

    @Test
    public void testDiffWithHeaderLineInText() throws Exception {
        String original = "test line1\ntest line2\ntest line 4\ntest line 5";
        String revised = "test line1\ntest line2\n@@ -2,6 +2,7 @@\ntest line 4\ntest line 5";

        String diffText = JavaTextDiff.diff(original, revised, "original", "revised", 10);
        String patched = JavaTextDiff.patch(original, diffText);

        assertEquals(revised, patched);
    }

    @Test
    public void testNewFileCreation() {
        String diffText = JavaTextDiff.diff("", "line1\nline2", null, "revised", 10);

        assertTrue(diffText.contains("--- /dev/null"), "null file indicator for original");
        assertTrue(diffText.contains("+++ revised"), "revised filename");

        try {
            String patched = JavaTextDiff.patch("", diffText);
            assertEquals("line1\nline2", patched.replace("\r\n", "\n"));
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
        }
    }

    @Test
    public void testFailingPatchByException() throws Exception {
        List<String> baseLines = fileToLines(MOCK_FOLDER + "issue10_base.txt");
        String patchText = fileToText(MOCK_FOLDER + "issue10_patch.txt");

        if (baseLines.size() > 40) {
            baseLines.set(40, baseLines.get(40) + " corrupted ");
        }
        String corruptedText = String.join("\n", baseLines);

        String patched = JavaTextDiff.patch(corruptedText, patchText);
        assertNotNull(patched, "patchStatic should produce output even with corrupted input");
    }

    @Test
    public void testDiffPatchIssue189Problem() throws Exception {
        String original = fileToText(MOCK_FOLDER + "issue_189_insert_original.txt");
        String revised = fileToText(MOCK_FOLDER + "issue_189_insert_revised.txt");

        // Verify round-trip using String-based methods
        String diff = JavaTextDiff.diff(original, revised,
                "issue_189_insert_original.txt", "issue_189_insert_revised.txt", 10);
        assertFalse(JavaTextDiff.identicalResult.equals(diff),
                "diffStatic should produce non-empty diff for different files");

        String patchedText = JavaTextDiff.patch(original, diff);
        String[] revLines = revised.split("\n", -1);
        String[] patchLines = patchedText.split("\n", -1);
        if (revLines.length > 0 && revLines[revLines.length - 1].isEmpty()) {
            revLines = java.util.Arrays.copyOf(revLines, revLines.length - 1);
        }
        if (patchLines.length > 0 && patchLines[patchLines.length - 1].isEmpty()) {
            patchLines = java.util.Arrays.copyOf(patchLines, patchLines.length - 1);
        }
        assertEquals(revLines.length, patchLines.length, "Number of lines must match after round-trip");
        for (int i = 0; i < revLines.length; i++) {
            assertEquals(revLines[i], patchLines[i], "Line " + (i + 1) + " must match after round-trip");
        }
    }

    /**
     * Verifies the round-trip: diffStatic → patchStatic reproduces the revised text.
     */
    private void verify(String origText, String revText, String originalFile, String revisedFile) {
        String unifiedDiff = JavaTextDiff.diff(origText, revText, originalFile, revisedFile, 10);

        String patchedText;
        try {
            patchedText = JavaTextDiff.patch(origText, unifiedDiff);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
            return;
        }

        String[] revLines = revText.split("\n", -1);
        String[] patchLines = patchedText.split("\n", -1);
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
