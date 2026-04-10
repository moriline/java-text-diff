package org.unidiffstatic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for computing differences between sequences and applying patches.
 * Required java 21+
 */
public final class JavaTextDiff {

    /**
     * Default line delimiter used for splitting and joining text.
     */
    public static final String defaultDelimiter = "\n";

    /**
     * Result returned when source and target texts are identical.
     */
    public static final String identicalResult = "";

    /**
     * Restores the original text from a revised text and a unified diff string.
     *
     * <p>This is the inverse of {@link #patch}.
     * For each hunk, the {@code +} lines (inserted) are removed and the {@code -}
     * lines (deleted) are restored.
     *
     * @param revised the revised (patched) text
     * @param uniDiff the unified diff string
     * @return the original text
     */
    public static String unpatch(String revised, String uniDiff) {
        return unpatch(revised, uniDiff, defaultDelimiter);
    }

    /**
     * Restores the original text from a revised text and a unified diff string.
     *
     * @param revised   the revised (patched) text
     * @param uniDiff   the unified diff string
     * @param delimiter the line delimiter used to split the revised text
     * @return the original text
     */
    public static String unpatch(String revised, String uniDiff, String delimiter) {
        List<String> revisedLines = new ArrayList<>(
                List.of(revised.split(Pattern.quote(delimiter), -1)));
        if (!revisedLines.isEmpty() && revisedLines.getLast().isEmpty()) {
            revisedLines.removeLast();
        }

        List<String> diffLines = List.of(uniDiff.split(defaultDelimiter, -1));
        if (!diffLines.isEmpty() && diffLines.getLast().isEmpty()) {
            diffLines = diffLines.subList(0, diffLines.size() - 1);
        }

        List<Hunk> hunks = parseHunks(diffLines);
        for (int h = hunks.size() - 1; h >= 0; h--) {
            Hunk hunk = hunks.get(h);
            int newCount = hunk.newCount();
            List<String> oldLines = hunk.oldLines();

            int newStart = hunk.newStart();
            int end = Math.min(newStart + newCount, revisedLines.size());
            revisedLines.subList(newStart, end).clear();
            revisedLines.addAll(newStart, oldLines);
        }

        return String.join(delimiter, revisedLines);
    }

    /**
     * Applies a unified diff string to the original text and returns the patched result.
     * Uses "\n" as line delimiter.
     *
     * <p>Usage:
     * <pre>{@code
     * String patchedText = UniDiffStatic.patchStatic(originalText, uniDiff);
     * }</pre>
     *
     * @param original the original text
     * @param uniDiff  the unified diff string to apply
     * @return the patched text
     * @throws Exception if the patch cannot be applied
     */
    public static String patch(String original, String uniDiff)
            throws Exception {
        return patch(original, uniDiff, defaultDelimiter);
    }

    /**
     * Applies a unified diff string to the original text and returns the patched result.
     *
     * @param original  the original text
     * @param uniDiff   the unified diff string to apply
     * @param delimiter the line delimiter used to split and join text
     * @return the patched text
     * @throws Exception if the patch cannot be applied
     */
    public static String patch(String original, String uniDiff, String delimiter)
            throws Exception {

        List<String> originalLines = new ArrayList<>(
                List.of(original.split(Pattern.quote(delimiter), -1)));
        if (!originalLines.isEmpty() && originalLines.getLast().isEmpty()) {
            originalLines.removeLast();
        }

        List<String> diffLines = List.of(uniDiff.split(defaultDelimiter, -1));
        if (!diffLines.isEmpty() && diffLines.getLast().isEmpty()) {
            diffLines = diffLines.subList(0, diffLines.size() - 1);
        }

        List<Hunk> hunks = parseHunks(diffLines);
        for (int h = hunks.size() - 1; h >= 0; h--) {
            Hunk hunk = hunks.get(h);
            int oldCount = hunk.oldCount();
            List<String> newLines = hunk.newLines();

            int end = Math.min(hunk.oldStart + oldCount, originalLines.size());
            originalLines.subList(hunk.oldStart, end).clear();
            originalLines.addAll(hunk.oldStart, newLines);
        }

        return String.join(delimiter, originalLines);
    }

    private static List<Hunk> parseHunks(List<String> diffLines) {
        List<Hunk> hunks = new ArrayList<>();
        boolean inPrelude = true;
        Hunk current = null;

        for (String line : diffLines) {
            if (inPrelude) {
                if (line.startsWith("+++")) inPrelude = false;
                continue;
            }

            if (line.startsWith("@@")) {
                if (current != null) hunks.add(current);
                int dashIdx = line.indexOf('-');
                int commaIdx = line.indexOf(',', dashIdx);
                int plusIdx = line.indexOf('+', commaIdx);
                int comma2Idx = line.indexOf(',', plusIdx);
                int oldStart = Integer.parseInt(line.substring(dashIdx + 1, commaIdx));
                int newStart = Integer.parseInt(line.substring(plusIdx + 1, comma2Idx));
                current = new Hunk(oldStart - 1, newStart - 1);
                continue;
            }

            if (current != null) {
                if (line.isEmpty()) {
                    current.lines.add(new HunkLine(' ', ""));
                } else {
                    char tag = line.charAt(0);
                    String content = line.length() > 1 ? line.substring(1) : "";
                    if (tag == ' ' || tag == '-' || tag == '+') {
                        current.lines.add(new HunkLine(tag, content));
                    }
                }
            }
        }
        if (current != null) hunks.add(current);
        return hunks;
    }

    private static final class Hunk {
        final int oldStart;
        final int newStart;
        final List<HunkLine> lines = new ArrayList<>();

        Hunk(int oldStart, int newStart) {
            this.oldStart = oldStart;
            this.newStart = newStart;
        }

        int newStart() { return newStart; }

        int oldCount() {
            int count = 0;
            for (HunkLine hl : lines) {
                if (hl.tag == ' ' || hl.tag == '-') count++;
            }
            return count;
        }

        int newCount() {
            int count = 0;
            for (HunkLine hl : lines) {
                if (hl.tag == ' ' || hl.tag == '+') count++;
            }
            return count;
        }

        List<String> newLines() {
            List<String> result = new ArrayList<>();
            for (HunkLine hl : lines) {
                if (hl.tag == ' ' || hl.tag == '+') result.add(hl.content);
            }
            return result;
        }

        List<String> oldLines() {
            List<String> result = new ArrayList<>();
            for (HunkLine hl : lines) {
                if (hl.tag == ' ' || hl.tag == '-') result.add(hl.content);
            }
            return result;
        }
    }

    private static final class HunkLine {
        final char tag;
        final String content;
        HunkLine(char tag, String content) { this.tag = tag; this.content = content; }
    }

    /**
     * Computes the difference between two texts and returns the result in unified diff format.
     * Uses "\n" as line delimiter and 3 context lines.
     *
     * <p>Usage:
     * <pre>{@code
     * String diffOutput = UniDiffStatic.diff(sourceText, targetText);
     * }</pre>
     *
     * @param sourceText the original text
     * @param targetText the revised text
     * @return unified diff string, or empty string if texts are identical
     */
    public static String diff(String sourceText, String targetText) {
        return diff(sourceText, targetText, "original", "revised", 3, defaultDelimiter);
    }

    /**
     * Computes the difference between two texts and returns the result in unified diff format.
     * Uses the given delimiter for splitting/joining lines and 3 context lines.
     */
    public static String diff(String sourceText, String targetText, String delimiter) {
        return diff(sourceText, targetText, "original", "revised", 3, delimiter);
    }

    /**
     * Computes the difference between two texts and returns the result in unified diff format.
     */
    public static String diff(
            String sourceText,
            String targetText,
            String sourceFileName,
            String targetFileName,
            int contextSize) {
        return diff(sourceText, targetText, sourceFileName, targetFileName, contextSize, defaultDelimiter);
    }

    /**
     * Computes the difference between two texts and returns the result in unified diff format.
     *
     * @param sourceText      the original text
     * @param targetText      the revised text
     * @param sourceName      name for the original file (shown in --- line)
     * @param targetName      name for the revised file (shown in +++ line)
     * @param contextSize     number of context lines around each change
     * @param delimiter       the line delimiter for splitting/joining
     * @return unified diff string, or empty string if texts are identical
     */
    public static String diff(
            String sourceText, String targetText,
            String sourceName, String targetName,
            int contextSize, String delimiter) {

        String linePattern = Pattern.quote(delimiter != null ? delimiter : defaultDelimiter);
        List<String> sourceLines = List.of(sourceText.split(linePattern, -1));
        if (!sourceLines.isEmpty() && sourceLines.getLast().isEmpty()) {
            sourceLines = sourceLines.subList(0, sourceLines.size() - 1);
        }

        List<String> targetLines = List.of(targetText.split(linePattern, -1));
        if (!targetLines.isEmpty() && targetLines.getLast().isEmpty()) {
            targetLines = targetLines.subList(0, targetLines.size() - 1);
        }

        if (sourceText.equals(targetText)) {
            return identicalResult;
        }

        List<DiffHunk> lineHunks = buildLineHunks(sourceLines, targetLines);
        if (lineHunks.isEmpty()) {
            return identicalResult;
        }

        return UnifiedDiffWriter.write(
                sourceName != null ? sourceName : "/dev/null",
                targetName != null ? targetName : "/dev/null",
                sourceLines,
                lineHunks,
                contextSize);
    }

    /**
     * Computes the difference between source and target lists.
     * implementation of Eugene Myers greedy differencing algorithm.
     * @param sourceLines
     * @param targetLines
     * @return
     */
    private static List<DiffHunk> buildLineHunks(
            List<String> sourceLines, List<String> targetLines) {

        List<DiffHunk> hunks = new ArrayList<>();
        int n = sourceLines.size();
        int m = targetLines.size();

        int maxD = n + m;
        int MAX = maxD + 1;
        int size = 1 + 2 * MAX;
        int middle = size / 2;
        MPathNode[] diagonal = new MPathNode[size];
        diagonal[middle + 1] = new MPathNode(0, -1, false, null);

        for (int d = 0; d <= maxD; d++) {
            for (int k = -d; k <= d; k += 2) {
                int mk = middle + k;
                int mkPlus = mk + 1;
                int mkMinus = mk - 1;

                MPathNode prev;
                int x;
                if (k == -d || (k != d && diagonal[mkMinus].x < diagonal[mkPlus].x)) {
                    x = diagonal[mkPlus].x;
                    prev = diagonal[mkPlus];
                } else {
                    x = diagonal[mkMinus].x + 1;
                    prev = diagonal[mkMinus];
                }
                diagonal[mkMinus] = null;

                int y = x - k;
                MPathNode node = new MPathNode(x, y, false, prev);

                while (x < n && y < m && sourceLines.get(x).equals(targetLines.get(y))) {
                    x++;
                    y++;
                }
                if (x > node.x) {
                    node = new MPathNode(x, y, true, node);
                }
                diagonal[mk] = node;

                if (x >= n && y >= m) {
                    return buildLineHunksFromRevision(node, sourceLines, targetLines);
                }
            }
            diagonal[middle + d - 1] = null;
        }
        return hunks;
    }

    private static List<DiffHunk> buildLineHunksFromRevision(
            MPathNode node, List<String> sourceLines, List<String> targetLines) {

        List<DiffHunk> hunks = new ArrayList<>();
        java.util.LinkedList<MLineOp> ops = new java.util.LinkedList<>();

        int trailingSnakeLen = 0;
        if (node.snake) {
            trailingSnakeLen = node.x - node.prev.x;
            node = node.prev;
        }

        while (node != null && node.prev != null && node.prev.y >= 0) {
            if (node.snake) {
                int snakeLen = node.x - node.prev.x;
                for (int i = snakeLen - 1; i >= 0; i--) {
                    ops.addFirst(new MLineOp.Keep(sourceLines.get(node.prev.x + i)));
                }
                node = node.prev;
                continue;
            }

            int x = node.x;
            int y = node.y;
            node = node.prev;
            int xAnchor = node.x;
            int yAnchor = node.y;

            if (xAnchor == x && yAnchor != y) {
                ops.addFirst(new MLineOp.Insert(targetLines.get(yAnchor)));
            } else if (xAnchor != x && yAnchor == y) {
                ops.addFirst(new MLineOp.Delete(sourceLines.get(xAnchor)));
            }
        }

        if (node != null && node.snake) {
            int snakeLen = node.x - node.prev.x;
            for (int i = snakeLen - 1; i >= 0; i--) {
                ops.addFirst(new MLineOp.Keep(sourceLines.get(node.prev.x + i)));
            }
        }

        if (trailingSnakeLen > 0 && node != null) {
            int startX = node.x;
            for (int i = 0; i < trailingSnakeLen; i++) {
                ops.addLast(new MLineOp.Keep(sourceLines.get(startX + i)));
            }
        }

        if (ops.isEmpty() && sourceLines.equals(targetLines)) {
            for (int i = 0; i < sourceLines.size(); i++) {
                ops.addLast(new MLineOp.Keep(sourceLines.get(i)));
            }
        }

        // Convert ops to hunks: group consecutive non-Keep ops into a single hunk
        int pos = 0;
        int i = 0;
        List<MLineOp> opList = new java.util.ArrayList<>(ops);
        while (i < opList.size()) {
            MLineOp op = opList.get(i);
            switch (op) {
                case MLineOp.Keep(var line) -> {
                    pos++;
                    i++;
                }
                case MLineOp.Delete(var d) -> {
                    int startPos = pos;
                    List<String> srcLines = new ArrayList<>();
                    List<String> tgtLines = new ArrayList<>();

                    // Collect all consecutive change ops (Delete + Insert)
                    while (i < opList.size()) {
                        MLineOp cur = opList.get(i);
                        switch (cur) {
                            case MLineOp.Delete(var del) -> {
                                srcLines.add(del);
                                pos++; // Delete consumes a line from original
                                i++;
                            }
                            case MLineOp.Insert(var ins) -> {
                                tgtLines.add(ins);
                                // Insert does NOT consume from original
                                i++;
                            }
                            default -> { // Keep — stop collecting
                                break;
                            }
                        }
                        // If we hit a Keep, the inner switch falls through to default,
                        // but we need to break out of the while loop
                        if (cur instanceof MLineOp.Keep) break;
                    }

                    hunks.add(new DiffHunk(startPos, srcLines, tgtLines));
                }
                case MLineOp.Insert(var ins) -> {
                    // Insert without prior Delete — happens when text is added at current position
                    int startPos = pos;
                    List<String> tgtLines = new ArrayList<>();

                    while (i < opList.size() && opList.get(i) instanceof MLineOp.Insert ins2) {
                        tgtLines.add(ins2.line());
                        i++;
                    }

                    hunks.add(new DiffHunk(startPos, List.of(), tgtLines));
                }
            }
        }

        return hunks;
    }

    private sealed interface MLineOp {
        record Keep(String line) implements MLineOp {}
        record Insert(String line) implements MLineOp {}
        record Delete(String line) implements MLineOp {}
    }

    private static final class MPathNode {
        final int x;
        final int y;
        final boolean snake;
        final MPathNode prev;

        MPathNode(int x, int y, boolean snake, MPathNode prev) {
            this.x = x;
            this.y = y;
            this.snake = snake;
            this.prev = prev;
        }
    }

    /**
     * Represents a single diff hunk with position and changed lines.
     */
    public record DiffHunk(int position, List<String> sourceLines, List<String> targetLines) {}

    /**
     * Generates unified diff format output from a list of diff hunks.
     */
    private static final class UnifiedDiffWriter {

        private static final String NL = "\n";
        private static final char SPACE = ' ';

        static String write(
                String oldName,
                String newName,
                List<String> originalLines,
                List<DiffHunk> hunks,
                int contextSize) {

            if (hunks.isEmpty()) {
                return "--- " + (oldName != null ? oldName : "/dev/null") + NL
                        + "+++ " + (newName != null ? newName : "/dev/null") + NL;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("--- ").append(oldName != null ? oldName : "/dev/null").append(NL);
            sb.append("+++ ").append(newName != null ? newName : "/dev/null").append(NL);

            List<List<DiffHunk>> groups = groupHunks(hunks, contextSize);
            for (List<DiffHunk> group : groups) {
                writeGroup(sb, originalLines, group, contextSize);
            }

            return sb.toString();
        }

        private static List<List<DiffHunk>> groupHunks(List<DiffHunk> hunks, int contextSize) {
            List<List<DiffHunk>> groups = new ArrayList<>();
            List<DiffHunk> current = new ArrayList<>();
            current.add(hunks.get(0));

            for (int i = 1; i < hunks.size(); i++) {
                DiffHunk last = current.get(current.size() - 1);
                DiffHunk next = hunks.get(i);

                int lastEnd = last.position() + last.sourceLines().size();
                int nextStart = next.position();

                if (lastEnd + contextSize >= nextStart - contextSize) {
                    current.add(next);
                } else {
                    groups.add(current);
                    current = new ArrayList<>();
                    current.add(next);
                }
            }
            groups.add(current);
            return groups;
        }

        private static void writeGroup(
                StringBuilder sb,
                List<String> origLines,
                List<DiffHunk> group,
                int contextSize) {

            DiffHunk first = group.get(0);
            DiffHunk last = group.get(group.size() - 1);

            int contextStart = first.position() - contextSize;
            if (contextStart < 0) contextStart = 0;

            int origStart = first.position() + 1 - contextSize;
            if (origStart < 1) origStart = 1;

            int revStart = first.position() + 1 - contextSize;
            if (revStart < 1) revStart = 1;

            int origTotal = 0;
            int revTotal = 0;

            for (int i = contextStart; i < first.position(); i++) {
                origTotal++;
                revTotal++;
            }

            for (DiffHunk hunk : group) {
                origTotal += hunk.sourceLines().size();
                revTotal += hunk.targetLines().size();
            }

            int postStart = last.position() + last.sourceLines().size();
            int postEnd = postStart + contextSize;
            if (postEnd > origLines.size()) postEnd = origLines.size();
            for (int i = postStart; i < postEnd; i++) {
                origTotal++;
                revTotal++;
            }

            sb.append("@@ -").append(origStart).append(',').append(origTotal)
                    .append(" +").append(revStart).append(',').append(revTotal)
                    .append(" @@").append(NL);

            for (int i = contextStart; i < first.position(); i++) {
                sb.append(SPACE).append(origLines.get(i)).append(NL);
            }

            for (int g = 0; g < group.size(); g++) {
                DiffHunk hunk = group.get(g);
                writeHunkLines(sb, hunk);

                if (g < group.size() - 1) {
                    DiffHunk next = group.get(g + 1);
                    int intermediateStart = hunk.position() + hunk.sourceLines().size();
                    for (int i = intermediateStart; i < next.position(); i++) {
                        sb.append(SPACE).append(origLines.get(i)).append(NL);
                    }
                }
            }

            for (int i = postStart; i < postEnd; i++) {
                sb.append(SPACE).append(origLines.get(i)).append(NL);
            }
        }

        private static void writeHunkLines(StringBuilder sb, DiffHunk hunk) {
            for (String line : hunk.sourceLines()) {
                sb.append('-').append(line).append(NL);
            }
            for (String line : hunk.targetLines()) {
                sb.append('+').append(line).append(NL);
            }
        }
    }
}
