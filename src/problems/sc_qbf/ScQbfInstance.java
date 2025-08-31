package problems.sc_qbf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import solutions.Solution;

public class ScQbfInstance {
    public final int domainSize;
    public final double[][] A;
    public final List<Set<Integer>> sets;

    public ScQbfInstance(double[][] A) {
        this.domainSize = A.length;
        this.A = A;
        this.sets = new ArrayList<>();
    }

    public boolean isSolutionValid(Solution<Integer> solution) {
        if (solution == null) return false;
        if (domainSize == 0) return true;

        boolean[] covered = new boolean[domainSize];
        int coveredCount = 0;

        for (Integer idx : solution) {
            if (idx == null) continue;
            if (idx < 0 || idx >= sets.size()) {
                return false; // invalid subset index
            }
            for (Integer elem : sets.get(idx)) {
                if (!covered[elem]) {
                    covered[elem] = true;
                    coveredCount++;
                    if (coveredCount == domainSize) {
                        return true; // early exit if fully covered
                    }
                }
            }
        }

        return coveredCount == domainSize;
    }

    public static ScQbfInstance fromFile(String filePath) {
        try {
            List<String> raw = Files.readAllLines(Paths.get(filePath));
            // Trim and drop empty lines
            List<String> lines = new ArrayList<>();
            for (String s : raw) {
                String t = s.trim();
                if (!t.isEmpty()) lines.add(t);
            }
            int idx = 0;

            // n
            int n = Integer.parseInt(lines.get(idx++));
            // subset sizes
            String[] sizeTokens = lines.get(idx++).split("\\s+");
            int[] subsetSizes = new int[sizeTokens.length];
            for (int i = 0; i < sizeTokens.length; i++) {
                subsetSizes[i] = Integer.parseInt(sizeTokens[i]);
            }

            // allocate
            double[][] A = new double[n][n];
            ScQbfInstance inst = new ScQbfInstance(A);

            // subsets (1-based in file -> 0-based here)
            for (int s = 0; s < subsetSizes.length; s++) {
                String[] elems = lines.get(idx++).split("\\s+");
                if (elems.length != subsetSizes[s]) {
                    throw new IllegalArgumentException(
                        "Expected " + subsetSizes[s] + " elements in subset " + s + ", got " + elems.length);
                }
                Set<Integer> set = new HashSet<>(subsetSizes[s]);
                for (String tok : elems) {
                    int e = Integer.parseInt(tok);
                    if (e < 1 || e > n) {
                        throw new IllegalArgumentException("Subset element out of range: " + e);
                    }
                    set.add(e - 1);
                }
                inst.sets.add(set);
            }

            // upper-triangular matrix rows
            int row = 0;
            while (idx < lines.size() && row < n) {
                String[] vals = lines.get(idx++).split("\\s+");
                for (int k = 0; k < vals.length; k++) {
                    int col = row + k;
                    if (col >= n) break;
                    inst.A[row][col] = Double.parseDouble(vals[k]);
                }
                row++;
            }

            return inst;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read instance file: " + filePath, e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ScQbfInstance {\n");
        sb.append("  domainSize: ").append(domainSize).append("\n");
        sb.append("  sets: ").append(sets).append("\n");
        sb.append("  A matrix: ").append(A.length).append("x").append(A[0].length);
        if (domainSize <= 10) {
            sb.append("\n");
            for (int i = 0; i < A.length; i++) {
                sb.append("    [");
                for (int j = 0; j < A[i].length; j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(A[i][j]);
                }
                sb.append("]\n");
            }
        } else {
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

}
