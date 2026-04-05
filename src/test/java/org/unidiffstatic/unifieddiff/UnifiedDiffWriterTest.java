package org.unidiffstatic.unifieddiff;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.UniDiffStatic;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code UnifiedDiffWriterTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 */
public class UnifiedDiffWriterTest {

    @Test
    public void testWrite() {
        String original = "hello\nworld\nfoo";
        String revised = "hello\nbar\nfoo";

        String unifiedDiff = UniDiffStatic.diff(original, revised, "original.txt", "revised.txt", 5);
        assertNotNull(unifiedDiff);
        assertFalse(unifiedDiff.isEmpty());
        assertTrue(unifiedDiff.contains("--- original.txt"));
        assertTrue(unifiedDiff.contains("+++ revised.txt"));
        System.out.println(unifiedDiff);
    }

    /**
     * Issue 47 — new file creation (from empty original).
     */
    @Test
    public void testWriteWithNewFile() {
        String unifiedDiff = UniDiffStatic.diff("", "line1\nline2", null, "revised", 5);

        String[] lines = unifiedDiff.split("\n");
        assertEquals("--- /dev/null", lines[0]);
        assertEquals("+++ revised", lines[1]);
        assertEquals("@@ -1,0 +1,2 @@", lines[2]);
    }
}
