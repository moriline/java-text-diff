package org.unidiffstatic.patch;

import org.unidiffstatic.algorithm.Change;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes the patch holding all deltas between the original and revised texts.
 *
 * @param <T> The type of the compared elements.
 */
public final class Patch<T> {

    private final List<AbstractDelta<T>> deltas;

    public Patch() {
        this(10);
    }

    public Patch(int estimatedPatchSize) {
        deltas = new ArrayList<>(estimatedPatchSize);
    }

    /**
     * Applies the patch to the original list and returns the revised list.
     */
    public List<T> applyTo(List<? extends T> target) throws Exception {
        List<T> result = new ArrayList<>(target);
        // Apply deltas in reverse order (by position) to preserve positions
        List<AbstractDelta<T>> sorted = getDeltas();
        for (int i = sorted.size() - 1; i >= 0; i--) {
            sorted.get(i).applyTo(result);
        }
        return result;
    }

    /**
     * Restores the original list from the revised list.
     */
    public List<T> restore(List<? extends T> target) {
        List<T> result = new ArrayList<>(target);
        List<AbstractDelta<T>> sorted = getDeltas();
        for (int i = sorted.size() - 1; i >= 0; i--) {
            sorted.get(i).restore(result);
        }
        return result;
    }

    public void addDelta(AbstractDelta<T> delta) {
        deltas.add(delta);
    }

    public List<AbstractDelta<T>> getDeltas() {
        deltas.sort(java.util.Comparator.comparing(d -> d.getSource().getPosition()));
        return Collections.unmodifiableList(deltas);
    }

    @Override
    public String toString() {
        return "Patch{deltas=" + deltas + "}";
    }

    /**
     * Generates a Patch from the list of Changes produced by a diff algorithm.
     */
    public static <T> Patch<T> generate(List<? extends T> original, List<? extends T> revised, List<Change> changes) {
        return generate(original, revised, changes, false);
    }

    /**
     * Generates a Patch from the list of Changes.
     *
     * @param includeEquals whether to include EqualDelta entries for unchanged regions.
     */
    public static <T> Patch<T> generate(
            List<? extends T> original,
            List<? extends T> revised,
            List<Change> _changes,
            boolean includeEquals) {

        // First pass: merge consecutive changes of the same type that are adjacent
        List<Change> changes = mergeAdjacentChanges(_changes);

        Patch<T> patch = new Patch<>(changes.size());
        int startOriginal = 0;
        int startRevised = 0;

        if (includeEquals) {
            changes = new ArrayList<>(changes);
            changes.sort(java.util.Comparator.comparingInt(c -> c.startOriginal));
        }

        for (Change change : changes) {
            if (includeEquals && startOriginal < change.startOriginal) {
                patch.addDelta(new EqualDelta<T>(
                        buildChunk(startOriginal, change.startOriginal, original),
                        buildChunk(startRevised, change.startRevised, revised)));
            }

            Chunk<T> orgChunk = buildChunk(change.startOriginal, change.endOriginal, original);
            Chunk<T> revChunk = buildChunk(change.startRevised, change.endRevised, revised);
            switch (change.deltaType) {
                case DELETE:
                    patch.addDelta(new DeleteDelta<>(orgChunk, revChunk));
                    break;
                case INSERT:
                    patch.addDelta(new InsertDelta<>(orgChunk, revChunk));
                    break;
                case CHANGE:
                    patch.addDelta(new ChangeDelta<>(orgChunk, revChunk));
                    break;
                default:
                    break;
            }

            startOriginal = change.endOriginal;
            startRevised = change.endRevised;
        }

        if (includeEquals && startOriginal < original.size()) {
            patch.addDelta(new EqualDelta<T>(
                    buildChunk(startOriginal, original.size(), original),
                    buildChunk(startRevised, revised.size(), revised)));
        }

        return patch;
    }

    /**
     * Merge consecutive changes of the same type that are adjacent in both original and revised.
     */
    private static List<Change> mergeAdjacentChanges(List<Change> changes) {
        if (changes.isEmpty()) return changes;

        // Sort by startOriginal, then startRevised — Myers produces changes in reverse order
        List<Change> sorted = new ArrayList<>(changes);
        sorted.sort((a, b) -> {
            int cmp = Integer.compare(a.startOriginal, b.startOriginal);
            if (cmp != 0) return cmp;
            return Integer.compare(a.startRevised, b.startRevised);
        });

        List<Change> merged = new ArrayList<>();
        Change current = sorted.get(0);

        for (int i = 1; i < sorted.size(); i++) {
            Change next = sorted.get(i);
            if (current.deltaType == next.deltaType
                    && current.endOriginal == next.startOriginal
                    && current.endRevised == next.startRevised) {
                // Merge: extend current to cover next
                current = new Change(current.deltaType,
                        current.startOriginal, next.endOriginal,
                        current.startRevised, next.endRevised);
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    private static <T> Chunk<T> buildChunk(int start, int end, List<? extends T> data) {
        return new Chunk<>(start, new ArrayList<>(data.subList(start, end)));
    }
}
