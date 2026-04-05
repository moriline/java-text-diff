package org.unidiffstatic.patch;

/**
 * Exception thrown when a patch cannot be applied.
 */
public class PatchFailedException extends Exception {

    public PatchFailedException(String message) {
        super(message);
    }
}
