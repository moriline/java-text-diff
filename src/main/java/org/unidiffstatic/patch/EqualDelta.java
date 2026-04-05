package org.unidiffstatic.patch;

import java.util.List;

/**
 * An equal delta — source and target blocks are identical.
 */
public class EqualDelta<T> extends AbstractDelta<T> {

    public EqualDelta(Chunk<T> source, Chunk<T> target) {
        super(DeltaType.EQUAL, source, target);
    }

    @Override
    protected void applyTo(List<T> target) {
        // no-op: equal parts don't change anything
    }

    @Override
    protected void restore(List<T> target) {
        // no-op
    }

    @Override
    public AbstractDelta<T> withChunks(Chunk<T> original, Chunk<T> revised) {
        return new EqualDelta<>(original, revised);
    }

    @Override
    public String toString() {
        return "[EqualDelta, position: " + getSource().getPosition() + ", lines: " + getSource().getLines() + "]";
    }
}
