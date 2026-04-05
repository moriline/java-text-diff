package org.unidiffstatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for patch()/patchStatic() with Windows line endings (\r\n).
 */
class UniDiffStaticWindowsPatchTest {

    private static final String DL = "\r\n";

    @Test
    void testPatchRoundTripReturnsOriginal() throws Exception {
        String source = "hello" + DL + "world" + DL + "foo";
        String target = "hello" + DL + "earth" + DL + "foo";

        String diff = UniDiffStatic.diff(source, target, DL);
        assertFalse(diff.isEmpty());

        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testPatchInsertion() throws Exception {
        String source = "aaa" + DL + "ccc";
        String target = "aaa" + DL + "bbb" + DL + "ccc";

        String diff = UniDiffStatic.diff(source, target, DL);
        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testPatchDeletion() throws Exception {
        String source = "aaa" + DL + "bbb" + DL + "ccc";
        String target = "aaa" + DL + "ccc";

        String diff = UniDiffStatic.diff(source, target, DL);
        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testPatchMultipleChanges() throws Exception {
        String source = "The" + DL + "dog" + DL + "is" + DL + "brown";
        String target = "The" + DL + "fox" + DL + "is" + DL + "down";

        String diff = UniDiffStatic.diff(source, target, DL);
        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testPatchNoChangesReturnsOriginal() throws Exception {
        String text = "hello" + DL + "world";

        String diff = UniDiffStatic.diff(text, text, DL);

        assertTrue(diff.isEmpty());
        String patched = UniDiffStatic.patch(text, diff, DL);

        assertEquals(text, patched);
    }

    @Test
    void testPatchEmptySourceToNonEmpty() throws Exception {
        String source = "";
        String target = "hello" + DL + "world";

        String diff = UniDiffStatic.diff(source, target, DL);
        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testPatchNonEmptyToEmpty() throws Exception {
        String source = "hello" + DL + "world";
        String target = "";

        String diff = UniDiffStatic.diff(source, target, DL);
        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testDiffStaticThenPatchRoundTripLargeText() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("line ").append(i).append(DL);
        }
        String source = sb.toString().stripTrailing();

        String target = source
                .replace("line 10" + DL, "MODIFIED 10" + DL)
                .replace("line 50" + DL, "MODIFIED 50" + DL)
                .replace("line 90" + DL, "MODIFIED 90" + DL);

        String diff = UniDiffStatic.diff(source, target, DL);
        assertFalse(diff.isEmpty());

        String patched = UniDiffStatic.patch(source, diff, DL);

        assertEquals(target, patched);
    }

    @Test
    void testPatchCustomDelimiterRoundTrip() throws Exception {
        String source = "aaa;bbb;ccc";
        String target = "aaa;xxx;ccc";

        String diff = UniDiffStatic.diff(source, target, ";");
        assertFalse(diff.isEmpty());

        String patched = UniDiffStatic.patch(source, diff, ";");

        assertEquals(target, patched);
    }

    @Test
    void testPatchCustomDelimiterPipe() throws Exception {
        String source = "one|two|three";
        String target = "one|two|updated";

        String diff = UniDiffStatic.diff(source, target, "|");
        String patched = UniDiffStatic.patch(source, diff, "|");

        assertEquals(target, patched);
    }

    @Test
    void testPatchCustomDelimiterInsertion() throws Exception {
        String source = "a;c";
        String target = "a;b;c";

        String diff = UniDiffStatic.diff(source, target, ";");
        String patched = UniDiffStatic.patch(source, diff, ";");

        assertEquals(target, patched);
    }

    // ─── Unpatch tests ───

    @Test
    void testUnpatchRoundTripReturnsOriginal() throws Exception {
        String source = "hello" + DL + "world" + DL + "foo";
        String target = "hello" + DL + "earth" + DL + "foo";

        String diff = UniDiffStatic.diff(source, target, DL);
        assertFalse(diff.isEmpty());

        String restored = UniDiffStatic.unpatch(target, diff, DL);
        assertEquals(source, restored);
    }

    @Test
    void testUnpatchDeletionReturnsOriginal() throws Exception {
        String source = "aaa" + DL + "bbb" + DL + "ccc";
        String target = "aaa" + DL + "ccc";

        String diff = UniDiffStatic.diff(source, target, DL);
        String restored = UniDiffStatic.unpatch(target, diff, DL);

        assertEquals(source, restored);
    }

    @Test
    void testUnpatchMultipleChanges() throws Exception {
        String source = "The" + DL + "dog" + DL + "is" + DL + "brown";
        String target = "The" + DL + "fox" + DL + "is" + DL + "down";

        String diff = UniDiffStatic.diff(source, target, DL);
        String restored = UniDiffStatic.unpatch(target, diff, DL);

        assertEquals(source, restored);
    }

    @Test
    void testUnpatchCustomDelimiterRoundTrip() throws Exception {
        String source = "aaa;bbb;ccc";
        String target = "aaa;xxx;ccc";

        String diff = UniDiffStatic.diff(source, target, ";");
        String patched = UniDiffStatic.patch(source, diff, ";");
        String restored = UniDiffStatic.unpatch(patched, diff, ";");

        assertEquals(source, restored);
    }

    @Test
    void testUnpatchCustomDelimiterPipe() throws Exception {
        String source = "one|two|three";
        String target = "one|two|updated";

        String diff = UniDiffStatic.diff(source, target, "|");
        String patched = UniDiffStatic.patch(source, diff, "|");
        String restored = UniDiffStatic.unpatch(patched, diff, "|");

        assertEquals(source, restored);
    }
}
