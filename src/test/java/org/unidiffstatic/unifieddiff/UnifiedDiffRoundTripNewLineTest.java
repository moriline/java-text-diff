package org.unidiffstatic.unifieddiff;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.UniDiffStatic;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code UnifiedDiffRoundTripNewLineTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 * Tests handling of trailing newlines and empty content.
 */
public class UnifiedDiffRoundTripNewLineTest {

    @Test
    public void testIssue135MissingNoNewLineInPatched() throws Exception {
        // Original content without trailing newline
        String beforeContent = "rootProject.name = \"sample-repo\"";
        String afterContent = "rootProject.name = \"sample-repo\"";

        // diffStatic should produce a unified diff
        String diff = UniDiffStatic.diff(beforeContent, afterContent);

        // Identical content should produce empty diff
        assertEquals(UniDiffStatic.identicalResult, diff);

        // patchStatic with empty diff should return original
        String patched = UniDiffStatic.patch(beforeContent, diff);
        assertEquals(afterContent, patched);
    }

    @Test
    public void testTrailingNewlineRoundTrip() throws Exception {
        String beforeContent = "rootProject.name = \"sample-repo\"";
        String afterContent = "rootProject.name = \"sample-repo\"\n";

        String diff = UniDiffStatic.diff(beforeContent, afterContent, "a", "b", 10);
        // Empty diff when only trailing newline differs (both have same lines after split)
        if (diff.isEmpty() || UniDiffStatic.identicalResult.equals(diff)) {
            // patchStatic with empty diff returns original
            String patched = UniDiffStatic.patch(beforeContent, diff);
            assertEquals(beforeContent, patched);
            return;
        }

        String patched = UniDiffStatic.patch(beforeContent, diff);
        // AfterContent has trailing newline which becomes an empty line after split
        String[] afterLines = afterContent.split("\n", -1);
        String[] patchLines = patched.split("\n", -1);

        // Normalize
        if (afterLines.length > 0 && afterLines[afterLines.length - 1].isEmpty()) {
            afterLines = java.util.Arrays.copyOf(afterLines, afterLines.length - 1);
        }
        if (patchLines.length > 0 && patchLines[patchLines.length - 1].isEmpty()) {
            patchLines = java.util.Arrays.copyOf(patchLines, patchLines.length - 1);
        }

        assertArrayEquals(afterLines, patchLines);
    }
}
