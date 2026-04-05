package org.unidiffstatic.patch;

import java.util.List;

/**
 * A change (replacement) delta — a block of source lines is replaced by target lines.
 */
public class ChangeDelta<T> extends AbstractDelta<T> {

    public ChangeDelta(Chunk<T> source, Chunk<T> target) {
        super(DeltaType.CHANGE, source, target);
    }

    @Override
    protected void applyTo(List<T> target) {
        Chunk<T> src = getSource();
        Chunk<T> tgt = getTarget();
        for (int i = 0; i < src.size(); i++) {
            target.remove(src.getPosition());
        }
        target.addAll(src.getPosition(), tgt.getLines());
    }

    @Override
    protected void restore(List<T> target) {
        Chunk<T> tgt = getTarget();
        Chunk<T> src = getSource();
        for (int i = 0; i < tgt.size(); i++) {
            target.remove(tgt.getPosition());
        }
        target.addAll(tgt.getPosition(), src.getLines());
    }

    @Override
    public AbstractDelta<T> withChunks(Chunk<T> original, Chunk<T> revised) {
        return new ChangeDelta<>(original, revised);
    }

    @Override
    public String toString() {
        return "[ChangeDelta, position: " + getSource().getPosition() + ", lines: "
                + getSource().getLines() + " to " + getTarget().getLines() + "]";
    }
}
