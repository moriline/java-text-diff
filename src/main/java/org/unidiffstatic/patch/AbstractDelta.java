package org.unidiffstatic.patch;

import java.util.List;
import java.util.Objects;

/**
 * Abstract delta between a source and a target.
 *
 * @param <T> type of the compared elements.
 */
public abstract class AbstractDelta<T> {

    private final Chunk<T> source;
    private final Chunk<T> target;
    private final DeltaType type;

    public AbstractDelta(DeltaType type, Chunk<T> source, Chunk<T> target) {
        Objects.requireNonNull(source, "source chunk must not be null");
        Objects.requireNonNull(target, "target chunk must not be null");
        Objects.requireNonNull(type, "type must not be null");
        this.type = type;
        this.source = source;
        this.target = target;
    }

    public Chunk<T> getSource() {
        return source;
    }

    public Chunk<T> getTarget() {
        return target;
    }

    public DeltaType getType() {
        return type;
    }

    /**
     * Apply this delta to the target list.
     */
    protected abstract void applyTo(List<T> target);

    /**
     * Restore the original state from the target list.
     */
    protected abstract void restore(List<T> target);

    /**
     * Create a new delta of the actual instance with customized chunk data.
     */
    public abstract AbstractDelta<T> withChunks(Chunk<T> original, Chunk<T> revised);

    @Override
    public int hashCode() {
        return Objects.hash(this.source, this.target, this.type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final AbstractDelta<?> other = (AbstractDelta<?>) obj;
        if (!Objects.equals(this.source, other.source)) return false;
        if (!Objects.equals(this.target, other.target)) return false;
        return this.type == other.type;
    }
}
