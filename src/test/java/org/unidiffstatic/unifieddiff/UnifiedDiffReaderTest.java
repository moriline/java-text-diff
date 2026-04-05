package org.unidiffstatic.unifieddiff;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code UnifiedDiffReaderTest}.
 * Tests chunk header pattern parsing — the same regex used in our unified diff parsing.
 */
public class UnifiedDiffReaderTest {

    /**
     * The same chunk header regexp used in UniDiffStatic.patchStatic parsing.
     */
    private static final Pattern UNIFIED_DIFF_CHUNK_REGEXP =
            Pattern.compile("^@@\\s+-(\\d+)(?:,(\\d+))?\\s+\\+(\\d+)(?:,(\\d+))?\\s+@@.*$");

    @Test
    public void testChunkHeaderParsing() {
        Matcher matcher = UNIFIED_DIFF_CHUNK_REGEXP.matcher(
                "@@ -189,6 +189,7 @@ TOKEN: /* SQL Keywords. prefixed with K_ to avoid name clashes */");
        assertTrue(matcher.find());
        assertEquals("189", matcher.group(1));
        assertEquals("189", matcher.group(3));
    }

    @Test
    public void testChunkHeaderParsing2() {
        Matcher matcher = UNIFIED_DIFF_CHUNK_REGEXP.matcher("@@ -189,6 +189,7 @@");
        assertTrue(matcher.find());
        assertEquals("189", matcher.group(1));
        assertEquals("189", matcher.group(3));
    }

    @Test
    public void testChunkHeaderParsing3() {
        Matcher matcher = UNIFIED_DIFF_CHUNK_REGEXP.matcher("@@ -1,27 +1,27 @@");
        assertTrue(matcher.find());
        assertEquals("1", matcher.group(1));
        assertEquals("1", matcher.group(3));
    }

    @Test
    public void testSimplePattern() {
        Pattern pattern = Pattern.compile("^\\+\\+\\+\\s");
        Matcher m = pattern.matcher("+++ revised.txt");
        assertTrue(m.find());
    }
}
