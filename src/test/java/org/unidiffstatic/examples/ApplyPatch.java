package org.unidiffstatic.examples;

import org.junit.jupiter.api.Test;
import org.unidiffstatic.UniDiffStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Analog of {@code ApplyPatch} example using String-based
 * {@code patchStatic()} method.
 */
public class ApplyPatch {

    public static final String MOCK_FOLDER = "build/resources/test/mocks/";

    public static String fileToText(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    /**
     * Main example: apply a pre-made unified diff patch to original text.
     */
    @Test
    public void main() throws Exception {
        String original = fileToText(MOCK_FOLDER + "issue10_base.txt");
        String patchText = fileToText(MOCK_FOLDER + "issue10_patch.txt");

        // At first, we have the unified diff text as a String
        System.out.println("=== Patch Content (first 500 chars) ===");
        System.out.println(patchText.substring(0, Math.min(500, patchText.length())));

        // Then apply the patch to the original text using String-based API
        String result = UniDiffStatic.patch(original, patchText);

        System.out.println("=== Patched Result (first 500 chars) ===");
        System.out.println(result.substring(0, Math.min(500, result.length())));

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Verify that patching produces valid output.
     */
    @Test
    public void testApplyPatchProducesResult() throws Exception {
        String original = fileToText(MOCK_FOLDER + "issue10_base.txt");
        String patchText = fileToText(MOCK_FOLDER + "issue10_patch.txt");

        String result = UniDiffStatic.patch(original, patchText);
        assertNotNull(result, "patchStatic should produce output");
        assertFalse(result.isEmpty(), "patched result should not be empty");
    }
}
