package org.unidiffstatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniDiffStaticTest {
    @Test
    void testDiffStaticMultiLine() {
        var one = """
                one two
                three and some string
                four
                """;
        var two = """
                one two
                three and some string
                four
                """;
        String result = UniDiffStatic.diff(one, two);

        assertEquals("", result);
    }

    @Test
    void testDiffStaticIdenticalTextsReturnsEmpty() {
        String source = "line1\nline2\nline3";
        String target = "line1\nline2\nline3";

        String result = UniDiffStatic.diff(source, target);

        assertEquals("", result);
    }

    @Test
    void testDiffStaticSingleLineInsertionContainsPlusLine() {
        String source = "aaa\nccc";
        String target = "aaa\nbbb\nccc";

        String result = UniDiffStatic.diff(source, target);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("+bbb"));
    }

    @Test
    void testDiffStaticSingleLineDeletionContainsMinusLine() {
        String source = "aaa\nbbb\nccc";
        String target = "aaa\nccc";

        String result = UniDiffStatic.diff(source, target);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-bbb"));
    }

    @Test
    void testDiffStaticLineReplacementContainsMinusAndPlus() {
        String source = "aaa\nbbb\nccc";
        String target = "aaa\nzzz\nccc";

        String result = UniDiffStatic.diff(source, target);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-bbb"));
        assertTrue(result.contains("+zzz"));
    }

    @Test
    void testDiffStaticMultipleChangesContainsAllDeltas() {
        String source = "The\ndog\nis\nbrown";
        String target = "The\nfox\nis\ndown";

        String result = UniDiffStatic.diff(source, target);

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

        String result = UniDiffStatic.diff(source, target);

        assertTrue(result.startsWith("--- original\n+++ revised\n"));
        assertTrue(result.contains("@@"));
    }

    @Test
    void testDiffStaticWithCustomFileNamesUsesProvidedNames() {
        String source = "hello";
        String target = "world";

        String result = UniDiffStatic.diff(source, target, "a/src.txt", "b/src.txt", 3);

        assertTrue(result.startsWith("--- a/src.txt\n+++ b/src.txt\n"));
    }

    @Test
    void testDiffStaticContextLinesArePresentWithSpacePrefix() {
        String source = "line1\nline2\nline3\nline4\nline5";
        String target = "line1\nlineX\nline3\nline4\nline5";

        String result = UniDiffStatic.diff(source, target, "a", "b", 1);

        // Context lines should have space prefix
        assertTrue(result.contains(" line1"));
        assertTrue(result.contains(" line3"));
    }

    @Test
    void testDiffStaticEmptySourceAllLinesAreInserts() {
        String source = "";
        String target = "hello\nworld";

        String result = UniDiffStatic.diff(source, target);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("+hello"));
        assertTrue(result.contains("+world"));
    }

    @Test
    void testDiffStaticEmptyTargetAllLinesAreDeletes() {
        String source = "hello\nworld";
        String target = "";

        String result = UniDiffStatic.diff(source, target);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-hello"));
        assertTrue(result.contains("-world"));
    }

    @Test
    void testDiffStaticSingleLineDiff() {
        String source = "hello";
        String target = "world";

        String result = UniDiffStatic.diff(source, target);

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-hello"));
        assertTrue(result.contains("+world"));
    }

    @Test
    void testDiffStaticResultEndsWithNewline() {
        String source = "a\nb";
        String target = "a\nc";

        String result = UniDiffStatic.diff(source, target);

        assertTrue(result.endsWith("\n"));
    }

    // --- delimiter parameter tests ---

    @Test
    void testDiffStaticCustomDelimiterSplitsAndJoinsCorrectly() {
        String source = "aaa;bbb;ccc";
        String target = "aaa;zzz;ccc";

        String result = UniDiffStatic.diff(source, target, ";");

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-bbb"));
        assertTrue(result.contains("+zzz"));
    }

    @Test
    void testDiffStaticCustomDelimiterWithCustomContext() {
        String source = "a;b;c;d;e";
        String target = "a;x;c;d;e";

        String result = UniDiffStatic.diff(source, target, "a", "b", 1, ";");

        assertFalse(result.isEmpty());
        assertTrue(result.contains("-b"));
        assertTrue(result.contains("+x"));
    }

    @Test
    void testDiffStaticCustomDelimiterSplitsCorrectly() {
        String source = "aaa;bbb;ccc";
        String target = "aaa;zzz;ccc";

        String result = UniDiffStatic.diff(source, target, ";");

        // diffStatic always produces unified diff with \n lines (standard format)
        // The delimiter is only used for splitting the input text
        assertFalse(result.isEmpty());
        assertTrue(result.contains("-bbb"));
        assertTrue(result.contains("+zzz"));
    }
}
