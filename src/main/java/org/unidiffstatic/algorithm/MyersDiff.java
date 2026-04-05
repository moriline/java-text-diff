package org.unidiffstatic.algorithm;

import org.unidiffstatic.patch.DeltaType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * A clean-room implementation of Eugene Myers greedy differencing algorithm.
 *
 * @param <T> type of the compared elements.
 */
public final class MyersDiff<T> {

    private final BiPredicate<? super T, ? super T> equalizer;

    public MyersDiff() {
        this.equalizer = Object::equals;
    }

    public MyersDiff(BiPredicate<? super T, ? super T> equalizer) {
        this.equalizer = equalizer != null ? equalizer : Object::equals;
    }

    /**
     * Computes the difference between source and target lists.
     *
     * @param source  the original sequence
     * @param target  the revised sequence
     * @param progress optional progress listener (may be null)
     * @return list of changes
     */
    public List<Change> computeDiff(
            final List<? extends T> source,
            final List<? extends T> target,
            DiffAlgorithmListener progress) {

        if (source == null) throw new IllegalArgumentException("source list must not be null");
        if (target == null) throw new IllegalArgumentException("target list must not be null");

        if (progress != null) {
            progress.diffStart();
        }
        PathNode path = buildPath(source, target, progress);
        List<Change> result = buildRevision(path, source, target);
        if (progress != null) {
            progress.diffEnd();
        }
        return result;
    }

    /**
     * Computes the minimum diff path using Eugene Myers' greedy algorithm.
     */
    private PathNode buildPath(
            final List<? extends T> orig,
            final List<? extends T> rev,
            DiffAlgorithmListener progress) {

        final int N = orig.size();
        final int M = rev.size();
        final int MAX = N + M + 1;
        final int size = 1 + 2 * MAX;
        final int middle = size / 2;
        final PathNode[] diagonal = new PathNode[size];

        diagonal[middle + 1] = new PathNode(0, -1, true, true, null);
        for (int d = 0; d < MAX; d++) {
            if (progress != null) {
                progress.diffStep(d, MAX);
            }
            for (int k = -d; k <= d; k += 2) {
                final int kmiddle = middle + k;
                final int kplus = kmiddle + 1;
                final int kminus = kmiddle - 1;
                PathNode prev;
                int i;

                if ((k == -d) || (k != d && diagonal[kminus].i < diagonal[kplus].i)) {
                    i = diagonal[kplus].i;
                    prev = diagonal[kplus];
                } else {
                    i = diagonal[kminus].i + 1;
                    prev = diagonal[kminus];
                }

                diagonal[kminus] = null;

                int j = i - k;

                PathNode node = new PathNode(i, j, false, false, prev);

                while (i < N && j < M && equalizer.test(orig.get(i), rev.get(j))) {
                    i++;
                    j++;
                }

                if (i != node.i) {
                    node = new PathNode(i, j, true, false, node);
                }

                diagonal[kmiddle] = node;

                if (i >= N && j >= M) {
                    return diagonal[kmiddle];
                }
            }
            diagonal[middle + d - 1] = null;
        }
        throw new IllegalStateException("could not find a diff path");
    }

    /**
     * Constructs a list of Changes from a difference path.
     */
    private List<Change> buildRevision(
            PathNode actualPath,
            List<? extends T> orig,
            List<? extends T> rev) {

        if (actualPath == null) throw new IllegalArgumentException("path is null");
        if (orig == null) throw new IllegalArgumentException("original sequence is null");
        if (rev == null) throw new IllegalArgumentException("revised sequence is null");

        PathNode path = actualPath;
        List<Change> changes = new ArrayList<>();
        if (path.isSnake()) {
            path = path.prev;
        }
        while (path != null && path.prev != null && path.prev.j >= 0) {
            if (path.isSnake()) {
                throw new IllegalStateException("bad diffpath: found snake when looking for diff");
            }
            int i = path.i;
            int j = path.j;

            path = path.prev;
            int ianchor = path.i;
            int janchor = path.j;

            if (ianchor == i && janchor != j) {
                changes.add(new Change(DeltaType.INSERT, ianchor, i, janchor, j));
            } else if (ianchor != i && janchor == j) {
                changes.add(new Change(DeltaType.DELETE, ianchor, i, janchor, j));
            } else {
                changes.add(new Change(DeltaType.CHANGE, ianchor, i, janchor, j));
            }

            if (path.isSnake()) {
                path = path.prev;
            }
        }
        return changes;
    }

    /**
     * PathNode represents a node in the shortest-path tree of the edit graph.
     */
    static final class PathNode {
        final int i;
        final int j;
        final boolean snake;
        final boolean first;
        final PathNode prev;

        PathNode(int i, int j, boolean snake, boolean first, PathNode prev) {
            this.i = i;
            this.j = j;
            this.snake = snake;
            this.first = first;
            this.prev = prev;
        }

        boolean isSnake() {
            return snake;
        }

        boolean isFirst() {
            return first;
        }
    }
}
