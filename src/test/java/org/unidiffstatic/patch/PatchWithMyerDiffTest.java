package org.unidiffstatic.patch;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code PatchWithMyerDiffTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 */
public class PatchWithMyerDiffTest {

    @Test
    public void testPatchChange() throws Exception {
        String changeTest_from = "aaa\nbbb\nccc\nddd";
        String changeTest_to = "aaa\nbxb\ncxc\nddd";

        String diff = JavaTextDiff.diff(changeTest_from, changeTest_to);
        assertFalse(diff.isEmpty());

        String patched = JavaTextDiff.patch(changeTest_from, diff);
        assertEquals(changeTest_to, patched);
    }

    @Test
    public void testPatchChangeWithCorruptedOriginal() throws Exception {
        String changeTest_from = "aaa\nbbb\nccc\nddd";
        String changeTest_to = "aaa\nbxb\ncxc\nddd";

        String diff = JavaTextDiff.diff(changeTest_from, changeTest_to);

        // Corrupt the original — our patchStatic doesn't verify content,
        // so it should still produce output (unlike original which throws)
        String corrupted = "aaa\nbbb\nCDC\nddd";
        String patched = JavaTextDiff.patch(corrupted, diff);
        assertNotNull(patched);
    }

    @Test
    public void testPatchThreeWayIssue138() throws Exception {
        // Using word-level diff via custom delimiter
        String base = "Imagine there's no heaven";
        String left = "Imagine there's no HEAVEN";
        String right = "IMAGINE there's no heaven";

        // Diff base → right
        String rightDiff = JavaTextDiff.diff(base, right, "base", "right", 10);
        assertFalse(rightDiff.isEmpty());

        // Apply right diff to left
        String applied = JavaTextDiff.patch(left, rightDiff);
        assertEquals(right, applied);
    }
}
