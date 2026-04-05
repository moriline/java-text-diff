package org.unidiffstatic.algorithm;

import org.unidiffstatic.patch.DeltaType;

/**
 * Represents a single change between original and revised sequences.
 */
public class Change {

    public final DeltaType deltaType;
    public final int startOriginal;
    public final int endOriginal;
    public final int startRevised;
    public final int endRevised;

    public Change(DeltaType deltaType, int startOriginal, int endOriginal, int startRevised, int endRevised) {
        this.deltaType = deltaType;
        this.startOriginal = startOriginal;
        this.endOriginal = endOriginal;
        this.startRevised = startRevised;
        this.endRevised = endRevised;
    }
}
