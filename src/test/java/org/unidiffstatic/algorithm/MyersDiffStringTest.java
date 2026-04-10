package org.unidiffstatic.algorithm;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the String-based diff via public UniDiffStatic API.
 * The internal MyersDiffString class is private, so we test through
 * diffStatic(), patchStatic(), and UniDiffStatic.diff().
 */
class MyersDiffStringTest {

    @Test
    void testComputeDiffIdenticalStringsReturnsEmptyDiff() {
        String result = JavaTextDiff.diff("abc", "abc");
        assertEquals(JavaTextDiff.identicalResult, result);
    }

    @Test
    void testComputeDiffEmptyStringsReturnsEmptyDiff() {
        String result = JavaTextDiff.diff("", "");
        assertEquals(JavaTextDiff.identicalResult, result);
    }

    @Test
    void testComputeDiffSingleCharInsertion() throws Exception {
        String source = "ac";
        String target = "abc";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeDiffSingleCharDeletion() throws Exception {
        String source = "abc";
        String target = "ac";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeDiffSingleCharReplacement() throws Exception {
        String source = "abc";
        String target = "adc";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeDiffEmptyToNonEmptyAllInserts() throws Exception {
        String source = "";
        String target = "hello";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeDiffNonEmptyToEmptyAllDeletes() throws Exception {
        String source = "hello";
        String target = "";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeDiffClassicMyersExample() throws Exception {
        // Classic Myers example: ABCABBA → CBABAC
        String source = "ABCABBA";
        String target = "CBABAC";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeDiffInsertAtBeginning() throws Exception {
        String source = "es";
        String target = "fest";

        String diff = JavaTextDiff.diff(source, target);
        String patched = JavaTextDiff.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testComputeUnifiedDiffIdenticalReturnsEmpty() {
        String result = JavaTextDiff.diff("hello", "hello");
        assertEquals(JavaTextDiff.identicalResult, result);
    }

    @Test
    void testComputeUnifiedDiffWithChangesContainsHeaderAndOps() {
        String source = "abc\nxyz";
        String target = "adc\nxyz";

        String result = JavaTextDiff.diff(source, target, "old", "new", 3);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("--- old"));
        assertTrue(result.contains("+++ new"));
        assertTrue(result.contains("@@"));
        assertTrue(result.contains("-abc"));
        assertTrue(result.contains("+adc"));
    }

    @Test
    void testComputePatchReturnsPatchWithChanges() {
        // Access patch via diffStatic → patchStatic round-trip
        String source = "abc";
        String target = "adc";

        String diff = JavaTextDiff.diff(source, target);
        assertFalse(diff.isEmpty());

        try {
            String patched = JavaTextDiff.patch(source, diff);
            assertEquals(target, patched);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
