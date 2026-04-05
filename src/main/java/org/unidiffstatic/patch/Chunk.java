package org.unidiffstatic.patch;

import java.util.Arrays;
import java.util.List;

/**
 * Holds information about a part of text involved in the diff process.
 *
 * @param <T> The type of the compared elements.
 */
public final class Chunk<T> {

    private final int position;
    private List<T> lines;

    /**
     * Creates a chunk and saves a copy of affected lines.
     *
     * @param position the start position
     * @param lines    the affected lines
     */
    public Chunk(int position, List<T> lines) {
        this.position = position;
        this.lines = List.copyOf(lines);
    }

    /**
     * Creates a chunk and saves a copy of affected lines.
     *
     * @param position the start position
     * @param lines    the affected lines
     */
    @SafeVarargs
    public Chunk(int position, T... lines) {
        this.position = position;
        this.lines = List.of(lines);
    }

    public int getPosition() {
        return position;
    }

    public void setLines(List<T> lines) {
        this.lines = lines;
    }

    public List<T> getLines() {
        return lines;
    }

    public int size() {
        return lines.size();
    }

    public int last() {
        return getPosition() + size() - 1;
    }

    @Override
    public int hashCode() {
        return 31 * position + (lines == null ? 0 : lines.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Chunk<?> other = (Chunk<?>) obj;
        return position == other.position && (lines == null ? other.lines == null : lines.equals(other.lines));
    }

    @Override
    public String toString() {
        return "[position: " + position + ", size: " + size() + ", lines: " + lines + "]";
    }
}
