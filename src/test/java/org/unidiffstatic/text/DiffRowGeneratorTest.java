package org.unidiffstatic.text;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.JavaTextDiff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code DiffRowGeneratorTest} using String-based
 * {@code diffStatic()} / {@code patchStatic()} methods.
 */
public class DiffRowGeneratorTest {

    public static final String MOCK_FOLDER = "build/resources/test/mocks/";

    public static String fileToText(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    @Test
    public void testGeneratorDefault() throws Exception {
        String first = "anything \n \nother";
        String second = "anything\n\nother";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorDefault2() throws Exception {
        String first = "anything \n \nother";
        String second = "anything\n\nother";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorInlineDiff() throws Exception {
        String first = "anything \n \nother";
        String second = "anything\n\nother";
        String diff = JavaTextDiff.diff(first, second);
        assertFalse(diff.isEmpty());
        assertTrue(diff.contains("--- original"));
        assertTrue(diff.contains("+++ revised"));
    }

    @Test
    public void testGeneratorIgnoreWhitespaces() throws Exception {
        // With whitespace differences, verify diff is produced and round-trips
        String first = "anything \n \nother\nmore lines";
        String second = "anything\n\nother\nsome more lines";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorWithWordWrap() throws Exception {
        String first = "anything \n \nother";
        String second = "anything\n\nother";
        String diff = JavaTextDiff.diff(first, second, "a", "b", 1);
        assertFalse(diff.isEmpty());
        assertTrue(diff.contains("@@"));
    }

    @Test
    public void testGeneratorWithMerge() throws Exception {
        String first = "anything \n \nother";
        String second = "anything\n\nother";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorWithMerge2() throws Exception {
        verifyRoundTrip("Test", "ester");
    }

    @Test
    public void testGeneratorWithMerge3() throws Exception {
        String first = "test\nanything \n \nother";
        String second = "anything\n\nother\ntest\ntest2";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorWithMergeByWord4() throws Exception {
        verifyRoundTrip("Test", "ester");
    }

    @Test
    public void testGeneratorWithMergeByWord5() throws Exception {
        String first = "Test feature";
        String second = "ester feature best";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorExample1() throws Exception {
        String first = "This is a test senctence.";
        String second = "This is a test for diffutils.";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorExample2() throws Exception {
        String first = "This is a test senctence.\nThis is the second line.\nAnd here is the finish.";
        String second = "This is a test for diffutils.\nThis is the second line.";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorUnchanged() throws Exception {
        String first = "anything \n \nother";
        String second = "anything\n\nother";
        String diff = JavaTextDiff.diff(first, second, "a", "b", 1);
        assertTrue(diff.contains("@@"));
    }

    @Test
    public void testGeneratorIssue14() throws Exception {
        String first = "J. G. Feldstein, Chair";
        String second = "T. P. Pastor, Chair";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorIssue15() throws Exception {
        String first = fileToText(MOCK_FOLDER + "issue15_1.txt");
        String second = fileToText(MOCK_FOLDER + "issue15_2.txt");
        verifyRoundTrip(first, second);
    }

    @Test
    public void testGeneratorIssue22() throws Exception {
        String aa = "This is a test senctence.";
        String bb = "This is a test for diffutils.\nThis is the second line.";
        verifyRoundTrip(aa, bb);
    }

    @Test
    public void testGeneratorIssue222() throws Exception {
        String aa = "This is a test for diffutils.\nThis is the second line.";
        String bb = "This is a test senctence.";
        verifyRoundTrip(aa, bb);
    }

    @Test
    public void testGeneratorIssue223() throws Exception {
        String aa = "This is a test senctence.";
        String bb = "This is a test for diffutils.\nThis is the second line.\nAnd one more.";
        verifyRoundTrip(aa, bb);
    }

    @Test
    public void testGenerationIssue44reportLinesUnchangedProblem() throws Exception {
        String first = "<dt>To do</dt>";
        String second = "<dt>Done</dt>";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testIgnoreWhitespaceIssue66() throws Exception {
        String first = "This\tis\ta\ttest.";
        String second = "This is a test";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testIgnoreWhitespaceIssue662() throws Exception {
        String first = "This  is  a  test.";
        String second = "This is a test";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testIgnoreWhitespaceIssue64() throws Exception {
        String first = "test\n\ntestline";
        String second = "A new text line\n\nanother one";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testReplaceDiffsIssue63() throws Exception {
        String first = "This  is  a  test.";
        String second = "This is a test";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testProblemTooManyDiffRowsIssue65() throws Exception {
        String first = "Ich möchte nicht mit einem Bot sprechen.\nIch soll das schon wieder wiederholen?";
        String second = "Ich möchte nicht mehr mit dir sprechen. Leite mich weiter.\nKannst du mich zum Kundendienst weiterleiten?";
        verifyRoundTrip(first, second);
    }

    @Test
    public void testLinefeedInStandardTagsWithLineWidthIssue81() throws Exception {
        String original = "American bobtail jaguar. American bobtail bombay but turkish angora and tomcat.\n"
                + "Russian blue leopard. Lion. Tabby scottish fold for russian blue, so savannah yet lynx.\n"
                + "Bengal tiger panther but singapura but bombay munchkin for cougar.";
        String revised = "bobtail jaguar. American bobtail turkish angora and tomcat.\n"
                + "Russian blue leopard. Lion. Tabby scottish folded for russian blue, so savannah yettie? lynx.\n"
                + "Bengal tiger panther but singultura but bombay munchkin for cougar. And more.";
        verifyRoundTrip(original, revised);
    }

    @Test
    public void testIssue86WrongInlineDiff() throws Exception {
        String original = fileToText("build/resources/test/com/github/difflib/text/issue_86_original.txt");
        String revised = fileToText("build/resources/test/com/github/difflib/text/issue_86_revised.txt");
        verifyRoundTrip(original, revised);
    }

    @Test
    public void testCorrectChangeIssue114() throws Exception {
        String original = "A\nB\nC\nD\nE";
        String revised = "a\nC\n\nE";
        verifyRoundTrip(original, revised);
    }

    @Test
    public void testCorrectChangeIssue1142() throws Exception {
        String original = "A\nB\nC\nD\nE";
        String revised = "a\nC\n\nE";
        verifyRoundTrip(original, revised);
    }

    private void verifyRoundTrip(String first, String second) throws Exception {
        String diff = JavaTextDiff.diff(first, second);

        if (JavaTextDiff.identicalResult.equals(diff)) {
            if (!first.equals(second)) {
                fail("diffStatic returned empty for different texts");
            }
            return;
        }

        String patched = JavaTextDiff.patch(first, diff);

        String[] secondLines = second.split("\n", -1);
        String[] patchedLines = patched.split("\n", -1);

        if (secondLines.length > 0 && secondLines[secondLines.length - 1].isEmpty()) {
            secondLines = java.util.Arrays.copyOf(secondLines, secondLines.length - 1);
        }
        if (patchedLines.length > 0 && patchedLines[patchedLines.length - 1].isEmpty()) {
            patchedLines = java.util.Arrays.copyOf(patchedLines, patchedLines.length - 1);
        }

        assertEquals(secondLines.length, patchedLines.length,
                "Number of lines after patching must match revised text");
        for (int i = 0; i < secondLines.length; i++) {
            assertEquals(secondLines[i], patchedLines[i],
                    "Line " + (i + 1) + " after patching must match revised text");
        }
    }
}
