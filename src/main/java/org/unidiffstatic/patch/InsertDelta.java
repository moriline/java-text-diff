package org.unidiffstatic.patch;

import java.util.List;

/**
 * An insert delta — a block of target lines is inserted at a position.
 */
public class InsertDelta<T> extends AbstractDelta<T> {

    public InsertDelta(Chunk<T> source, Chunk<T> target) {
        super(DeltaType.INSERT, source, target);
    }

    @Override
    protected void applyTo(List<T> target) {
        Chunk<T> tgt = getTarget();
        Chunk<T> src = getSource();
        target.addAll(src.getPosition(), tgt.getLines());
    }

    @Override
    protected void restore(List<T> target) {
        Chunk<T> tgt = getTarget();
        for (int i = tgt.size() - 1; i >= 0; i--) {
            target.remove(tgt.getPosition() + i);
        }
    }

    @Override
    public AbstractDelta<T> withChunks(Chunk<T> original, Chunk<T> revised) {
        return new InsertDelta<>(original, revised);
    }

    @Override
    public String toString() {
        return "[InsertDelta, position: " + getSource().getPosition() + ", lines: " + getTarget().getLines() + "]";
    }
}
