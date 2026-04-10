package org.unidiffstatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for diffStatic/patchStatic with Windows line endings (\r\n).
 */
class UniDiffStaticWindowsTest {

    private static final String DL = "\r\n";

    @Test
    void testDiffStaticIdenticalTextsReturnsEmpty() {
        String source = "line1" + DL + "line2" + DL + "line3";
        String target = "line1" + DL + "line2" + DL + "line3";

        String result = JavaTextDiff.diff(source, target, DL);

        assertEquals("", result);
    }

    @Test
    void testDiffStaticSingleLineInsertionContainsPlusLine() {
        String source = "aaa" + DL + "ccc";
        String target = "aaa" + DL + "bbb" + DL + "ccc";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("+bbb"));
    }

    @Test
    void testDiffStaticSingleLineDeletionContainsMinusLine() {
        String source = "aaa" + DL + "bbb" + DL + "ccc";
        String target = "aaa" + DL + "ccc";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-bbb"));
    }

    @Test
    void testDiffStaticLineReplacementContainsMinusAndPlus() {
        String source = "aaa" + DL + "bbb" + DL + "ccc";
        String target = "aaa" + DL + "zzz" + DL + "ccc";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-bbb"));
        assertTrue(result.contains("+zzz"));
    }

    @Test
    void testDiffStaticMultipleChangesContainsAllDeltas() {
        String source = "The" + DL + "dog" + DL + "is" + DL + "brown";
        String target = "The" + DL + "fox" + DL + "is" + DL + "down";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-dog"));
        assertTrue(result.contains("+fox"));
        assertTrue(result.contains("-brown"));
        assertTrue(result.contains("+down"));
    }

    @Test
    void testDiffStaticHasUnifiedDiffHeader() {
        String source = "hello";
        String target = "world";

        String result = JavaTextDiff.diff(source, target, DL);

        assertTrue(result.startsWith("--- original\n+++ revised\n"));
        assertTrue(result.contains("@@"));
    }

    @Test
    void testDiffStaticWithCustomFileNamesUsesProvidedNames() {
        String source = "hello";
        String target = "world";

        String result = JavaTextDiff.diff(source, target, "a/src.txt", "b/src.txt", 3, DL);

        assertTrue(result.startsWith("--- a/src.txt\n+++ b/src.txt\n"));
    }

    @Test
    void testDiffStaticContextLinesArePresentWithSpacePrefix() {
        String source = "line1" + DL + "line2" + DL + "line3" + DL + "line4" + DL + "line5";
        String target = "line1" + DL + "lineX" + DL + "line3" + DL + "line4" + DL + "line5";

        String result = JavaTextDiff.diff(source, target, "a", "b", 1, DL);

        assertTrue(result.contains(" line1"));
        assertTrue(result.contains(" line3"));
    }

    @Test
    void testDiffStaticEmptySourceAllLinesAreInserts() {
        String source = "";
        String target = "hello" + DL + "world";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("+hello"));
        assertTrue(result.contains("+world"));
    }

    @Test
    void testDiffStaticEmptyTargetAllLinesAreDeletes() {
        String source = "hello" + DL + "world";
        String target = "";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-hello"));
        assertTrue(result.contains("-world"));
    }

    @Test
    void testDiffStaticSingleLineDiff() {
        String source = "hello";
        String target = "world";

        String result = JavaTextDiff.diff(source, target, DL);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-hello"));
        assertTrue(result.contains("+world"));
    }

    @Test
    void testDiffStaticResultEndsWithNewline() {
        String source = "a" + DL + "b";
        String target = "a" + DL + "c";

        String result = JavaTextDiff.diff(source, target, DL);

        assertTrue(result.endsWith("\n"));
    }
}
