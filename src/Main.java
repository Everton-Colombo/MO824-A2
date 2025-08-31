import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.solvers.GRASP_QBF;
import problems.sc_qbf.ScQbfInstance;
import solutions.Solution;

public class Main {

    private static void runInstance(String instancePath, double alpha, int iterations) throws IOException {
        long start = System.currentTimeMillis();
        GRASP_QBF grasp = new GRASP_QBF(alpha, iterations, instancePath);
        Solution<Integer> best = grasp.solve();
        long end = System.currentTimeMillis();

        String name = new File(instancePath).getName();
        double maxOriginal = -best.cost; // since we minimize the inverse
        System.out.println("Instance: " + name);
        System.out.println("  Best solution (inverse cost) = " + best.cost);
        System.out.println("  Best solution (original QBF) = " + maxOriginal);
        System.out.println("  Elements = " + Arrays.toString(best.toArray()));
        System.out.println("  Size = " + best.size());
        System.out.println("  Time = " + ((end - start) / 1000.0) + " s\n");
    }

    private static void runTests(String[] args) throws IOException{
        // Defaults
        double alpha = 0.05;
        int iterations = 1000;
        String path = "../instances/qbf"; // directory by default
        boolean verbose = false;

        // Parse args: [alpha] [iterations] [path] [--verbose]
        if (args.length >= 1) alpha = Double.parseDouble(args[0]);
        if (args.length >= 2) iterations = Integer.parseInt(args[1]);
        if (args.length >= 3) path = args[2];
        if (args.length >= 4 && "--verbose".equalsIgnoreCase(args[3])) verbose = true;

        AbstractGRASP.verbose = verbose;

        File f = new File(path);
        if (!f.exists()) {
            System.err.println("Path not found: " + path);
            System.exit(1);
        }

        System.out.println("Running GRASP-QBF");
        System.out.println("  alpha=" + alpha + ", iterations=" + iterations + ", verbose=" + verbose);

        if (f.isFile()) {
            runInstance(f.getPath(), alpha, iterations);
        } else if (f.isDirectory()) {
            File[] files = f.listFiles(file -> file.isFile() && file.getName().startsWith("qbf"));
            if (files == null || files.length == 0) {
                System.err.println("No qbf instances found in directory: " + path);
                System.exit(1);
            }
            Arrays.sort(files, Comparator.comparing(File::getName));
            for (File file : files) {
                runInstance(file.getPath(), alpha, iterations);
            }
        } else {
            System.err.println("Invalid path: " + path);
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException {
        // runTests(args);

        ScQbfInstance instance = ScQbfInstance.fromFile("1.txt");
        System.out.println(instance);
    }
}
