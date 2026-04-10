package org.unidiffstatic.patch;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code PatchWithAllDiffAlgorithmsTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 *
 * Since our API doesn't expose algorithm factories, we test the
 * String-based round-trip with different content patterns.
 */
public class PatchWithAllDiffAlgorithmsTest {

    @Test
    public void testPatchInsert() throws Exception {
        String insertTest_from = "hhh";
        String insertTest_to = "hhh\njjj\nkkk\nlll";

        String diff = JavaTextDiff.diff(insertTest_from, insertTest_to);
        String patched = JavaTextDiff.patch(insertTest_from, diff);

        assertEquals(insertTest_to, patched);
    }

    @Test
    public void testPatchDelete() throws Exception {
        String deleteTest_from = "ddd\nfff\nggg\nhhh";
        String deleteTest_to = "ggg";

        String diff = JavaTextDiff.diff(deleteTest_from, deleteTest_to);
        String patched = JavaTextDiff.patch(deleteTest_from, diff);

        assertEquals(deleteTest_to, patched);
    }

    @Test
    public void testPatchChange() throws Exception {
        String changeTest_from = "aaa\nbbb\nccc\nddd";
        String changeTest_to = "aaa\nbxb\ncxc\nddd";

        String diff = JavaTextDiff.diff(changeTest_from, changeTest_to);
        String patched = JavaTextDiff.patch(changeTest_from, diff);

        assertEquals(changeTest_to, patched);
    }

    // Serialization test removed — our String-based API doesn't require serializable patches.
    // The original test verified Patch<String> serialization with different algorithms.
    // Our diffStatic/patchStatic methods work with String input/output directly.
}
