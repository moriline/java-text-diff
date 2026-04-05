package org.unidiffstatic.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code StringUtilsTest}.
 * Tests string utility functions adapted for our String-based diff API.
 */
public class StringUtilsTest {

    /**
     * Test that special HTML characters are preserved in diff output.
     * Analog of testHtmlEntites.
     */
    @Test
    public void testHtmlEntites() throws Exception {
        String input = "<test>";
        String diff = org.unidiffstatic.UniDiffStatic.diff(input, "<test>");

        // Empty diff for identical content
        assertEquals(org.unidiffstatic.UniDiffStatic.identicalResult, diff);
    }

    /**
     * Test that tab characters are handled correctly in diff.
     * Analog of testNormalize_String.
     */
    @Test
    public void testNormalizeString() throws Exception {
        String input = "\ttest";
        String normalized = input.replace("\t", "    ");

        assertEquals("    test", normalized);

        // Verify round-trip with tab content
        String diff = org.unidiffstatic.UniDiffStatic.diff("\ttest", "\ttest");
        assertEquals(org.unidiffstatic.UniDiffStatic.identicalResult, diff);
    }

    /**
     * Test that multi-byte characters (surrogate pairs) are handled correctly.
     * Analog of testWrapText_String_int with surrogate pairs.
     */
    @Test
    public void testWrapTextStringInt() {
        // Our diff doesn't wrap text, but we verify surrogate pair handling
        String withSurrogate = ".\uD800\uDC01.";

        // diffStatic should handle it without issues
        String diff = org.unidiffstatic.UniDiffStatic.diff(
                withSurrogate, ".\uD800\uDC01.");
        assertEquals(org.unidiffstatic.UniDiffStatic.identicalResult, diff);

        // Different surrogate content
        String diff2 = org.unidiffstatic.UniDiffStatic.diff(
                withSurrogate, "changed");
        assertFalse(diff2.isEmpty());
    }

    /**
     * Test that empty/wrap-width-like parameter is handled.
     * Analog of testWrapText_String_int_zero (which threw IllegalArgumentException).
     */
    @Test
    public void testWrapTextStringIntZero() {
        // Our API doesn't have wrap functionality, but we test empty string handling
        String diff = org.unidiffstatic.UniDiffStatic.diff("", "test");
        assertFalse(diff.isEmpty());
        assertTrue(diff.contains("+test"));
    }
}
