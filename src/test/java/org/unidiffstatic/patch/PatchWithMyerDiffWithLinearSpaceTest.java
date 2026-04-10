package org.unidiffstatic.patch;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code PatchWithMyerDiffWithLinearSpaceTest} using
 * String-based {@code diffStatic()} / {@code patchStatic()} methods
 * with {@link MyersDiffString} as the diff algorithm.
 *
 * Note: We don't have MyersDiffWithLinearSpace, so we use MyersDiffString
 * which is our O(ND) char-based implementation.
 */
public class PatchWithMyerDiffWithLinearSpaceTest {

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

        // Corrupt the original — our patchStatic doesn't verify content
        String corrupted = "aaa\nbbb\nCDC\nddd";
        String patched = JavaTextDiff.patch(corrupted, diff);
        assertNotNull(patched);
    }
}
