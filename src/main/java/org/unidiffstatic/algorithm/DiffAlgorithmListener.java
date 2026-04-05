package org.unidiffstatic.algorithm;

/**
 * Listener interface for diff algorithm progress notifications.
 */
public interface DiffAlgorithmListener {

    /**
     * Called when the diff computation starts.
     */
    void diffStart();

    /**
     * Called at each step of the diff computation.
     *
     * @param value current step value
     * @param max   maximum number of steps
     */
    void diffStep(int value, int max);

    /**
     * Called when the diff computation ends.
     */
    void diffEnd();
}
