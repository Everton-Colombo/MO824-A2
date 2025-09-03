package problems.sc_qbf;

import solutions.Solution;

public class Main {
    public static void main(String[] args) {
        // GRASP parameters
        double alpha = 0.1;  // Greediness parameter (0.0 = greedy, 1.0 = random)
        int iterations = 1000;  // Number of GRASP iterations
        
        // Instance file path
        String instancePath = "instances/scqbf/1.txt";
        
        try {
            // Create GRASP solver
            GraspScQbf grasp = new GraspScQbf(alpha, iterations, instancePath);
            
            // Execute GRASP
            System.out.println("Starting GRASP execution...");
            System.out.println("Alpha: " + alpha);
            System.out.println("Iterations: " + iterations);
            System.out.println("Instance: " + instancePath);
            System.out.println();
            
            long startTime = System.currentTimeMillis();
            Solution<Integer> bestSolution = grasp.solve();
            long endTime = System.currentTimeMillis();
            
            // Print results
            System.out.println("GRASP execution completed!");
            System.out.println("Best solution cost: " + bestSolution.cost);
            System.out.println("Solution size: " + bestSolution.size());
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
            System.out.println("Best solution: " + bestSolution);
            
        } catch (Exception e) {
            System.err.println("Error executing GRASP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
