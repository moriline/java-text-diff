package org.unidiffstatic.patch;

import java.util.List;

/**
 * A delete delta — a block of source lines is removed.
 */
public class DeleteDelta<T> extends AbstractDelta<T> {

    public DeleteDelta(Chunk<T> source, Chunk<T> target) {
        super(DeltaType.DELETE, source, target);
    }

    @Override
    protected void applyTo(List<T> target) {
        Chunk<T> src = getSource();
        for (int i = src.size() - 1; i >= 0; i--) {
            target.remove(src.getPosition());
        }
    }

    @Override
    protected void restore(List<T> target) {
        Chunk<T> src = getSource();
        target.addAll(src.getPosition(), src.getLines());
    }

    @Override
    public AbstractDelta<T> withChunks(Chunk<T> original, Chunk<T> revised) {
        return new DeleteDelta<>(original, revised);
    }

    @Override
    public String toString() {
        return "[DeleteDelta, position: " + getSource().getPosition() + ", lines: " + getSource().getLines() + "]";
    }
}
