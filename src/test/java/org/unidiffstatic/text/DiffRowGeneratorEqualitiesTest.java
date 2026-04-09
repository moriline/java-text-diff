package org.unidiffstatic.text;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.UniDiffStatic;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code DiffRowGeneratorEqualitiesTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 */
public class DiffRowGeneratorEqualitiesTest {

    @Test
    public void testDefaultEqualityProcessingLeavesTextUnchanged() {
        String text = "hello world";
        String diff = UniDiffStatic.diff(text, text);

        assertEquals(UniDiffStatic.identicalResult, diff);

        try {
            String patched = UniDiffStatic.patch(text, diff);
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
        String diff = UniDiffStatic.diff(first, second);
        assertFalse(diff.isEmpty());

        // Round-trip should work
        try {
            String patched = UniDiffStatic.patch(first, diff);
            assertEquals(second, patched);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
        }
    }

    @Test
    public void testHtmlEscapingEqualitiesWorksWithDefaultNormalizer() {
        String text = "hello <world>";
        String diff = UniDiffStatic.diff(text, text);

        assertEquals(UniDiffStatic.identicalResult, diff);

        try {
            String patched = UniDiffStatic.patch(text, diff);
            assertEquals(text, patched);
        } catch (Exception e) {
            fail("patchStatic failed: " + e.getMessage());
        }
    }

    @Test
    public void testEqualitiesProcessedButInlineDiffStillPresent() throws Exception {
        String first = "hello world";
        String second = "hello there";

        String diff = UniDiffStatic.diff(first, second);
        assertFalse(diff.isEmpty());

        String patched = UniDiffStatic.patch(first, diff);
        assertEquals(second, patched);
    }
}
