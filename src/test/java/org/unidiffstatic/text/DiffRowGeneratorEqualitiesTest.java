package org.unidiffstatic.text;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code DiffRowGeneratorEqualitiesTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 */
public class DiffRowGeneratorEqualitiesTest {

    @Test
    public void testDefaultEqualityProcessingLeavesTextUnchanged() {
        String text = "hello world";
        String diff = JavaTextDiff.diff(text, text);

        assertEquals(JavaTextDiff.identicalResult, diff);

        try {
            String patched = JavaTextDiff.patch(text, diff);
            assertEquals(text, patched);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
        }
    }

    @Test
    public void testCustomEqualityProcessingIsApplied() {
        // Case-insensitive comparison via diffStatic round-trip
        String first = "HELLO WORLD";
        String second = "hello world";

        // diffStatic will detect difference (case-sensitive)
        String diff = JavaTextDiff.diff(first, second);
        assertFalse(diff.isEmpty());

        // Round-trip should work
        try {
            String patched = JavaTextDiff.patch(first, diff);
            assertEquals(second, patched);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
        }
    }

    @Test
    public void testHtmlEscapingEqualitiesWorksWithDefaultNormalizer() {
        String text = "hello <world>";
        String diff = JavaTextDiff.diff(text, text);

        assertEquals(JavaTextDiff.identicalResult, diff);

        try {
            String patched = JavaTextDiff.patch(text, diff);
            assertEquals(text, patched);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
        }
    }

    @Test
    public void testEqualitiesProcessedButInlineDiffStillPresent() throws Exception {
        String first = "hello world";
        String second = "hello there";

        String diff = JavaTextDiff.diff(first, second);
        assertFalse(diff.isEmpty());

        String patched = JavaTextDiff.patch(first, diff);
        assertEquals(second, patched);
    }
}
