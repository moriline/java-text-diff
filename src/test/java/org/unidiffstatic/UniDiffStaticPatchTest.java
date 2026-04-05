package org.unidiffstatic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for patch()/patchStatic().
 */
class UniDiffStaticPatchTest {

    @Test
    void testPatchRoundTripReturnsOriginal() throws Exception {
        String source = "hello\nworld\nfoo";
        String target = "hello\nearth\nfoo";

        String diff = UniDiffStatic.diff(source, target);
        assertFalse(diff.isEmpty());

        String patched = UniDiffStatic.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testPatchInsertion() throws Exception {
        String source = "aaa\nccc";
        String target = "aaa\nbbb\nccc";

        String diff = UniDiffStatic.diff(source, target);
        String patched = UniDiffStatic.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testPatchDeletion() throws Exception {
        String source = "aaa\nbbb\nccc";
        String target = "aaa\nccc";

        String diff = UniDiffStatic.diff(source, target);
        String patched = UniDiffStatic.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testPatchMultipleChanges() throws Exception {
        String source = "The\ndog\nis\nbrown";
        String target = "The\nfox\nis\ndown";

        String diff = UniDiffStatic.diff(source, target);
        String patched = UniDiffStatic.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testPatchNoChangesReturnsOriginal() throws Exception {
        String text = "hello\nworld";

        String diff = UniDiffStatic.diff(text, text);

        assertTrue(diff.isEmpty());
        String patched = UniDiffStatic.patch(text, diff);

        assertEquals(text, patched);
    }

    @Test
    void testPatchEmptySourceToNonEmpty() throws Exception {
        String source = "";
        String target = "hello\nworld";

        String diff = UniDiffStatic.diff(source, target);
        String patched = UniDiffStatic.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testPatchNonEmptyToEmpty() throws Exception {
        String source = "hello\nworld";
        String target = "";

        String diff = UniDiffStatic.diff(source, target);
        String patched = UniDiffStatic.patch(source, diff);

        assertEquals(target, patched);
    }

    @Test
    void testDiffStaticThenPatchRoundTripLargeText() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("line ").append(i).append("\n");
        }
        String source = sb.toString().stripTrailing();

        String target = source
                .replace("line 10\n", "MODIFIED 10\n")
                .replace("line 50\n", "MODIFIED 50\n")
                .replace("line 90\n", "MODIFIED 90\n");

        String diff = UniDiffStatic.diff(source, target);
        assertFalse(diff.isEmpty());

        String patched = UniDiffStatic.patch(source, diff);

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
        String source = "hello\nworld\nfoo";
        String target = "hello\nearth\nfoo";

        String diff = UniDiffStatic.diff(source, target);
        assertFalse(diff.isEmpty());

        String restored = UniDiffStatic.unpatch(target, diff);
        assertEquals(source, restored);
    }

    @Test
    void testUnpatchDeletionReturnsOriginal() throws Exception {
        String source = "aaa\nbbb\nccc";
        String target = "aaa\nccc";

        String diff = UniDiffStatic.diff(source, target);
        String restored = UniDiffStatic.unpatch(target, diff);

        assertEquals(source, restored);
    }

    @Test
    void testUnpatchMultipleChanges() throws Exception {
        String source = "The\ndog\nis\nbrown";
        String target = "The\nfox\nis\ndown";

        String diff = UniDiffStatic.diff(source, target);
        String restored = UniDiffStatic.unpatch(target, diff);

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
