package problems.sc_qbf;

import java.util.Set;
import problems.Evaluator;
import solutions.ScQbfSolution;
import solutions.Solution;

public class ScQbfEvaluator implements Evaluator<Integer> {

    public ScQbfInstance problemInstance;

    public ScQbfEvaluator(ScQbfInstance problemInstance) {
        this.problemInstance = problemInstance;
    }

    public boolean isSolutionValid(Solution<Integer> solution) {
        return evaluateCoverage(solution) == 1.0;
    }

    @Override
    public Integer getDomainSize() {
        return problemInstance.domainSize;
    }

    @Override
    public Double evaluate(Solution<Integer> sol) {
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        Double sum = 0.0;
        
        // Calculate QBF value directly from solution indices
        for (Integer i : sol) {
            for (Integer j : sol) {
                sum += problemInstance.A[i][j];
            }
        }
        
        sol.cost = sum;
        return sol.cost;
    }

    @Override
    public Double evaluateInsertionCost(Integer elem, Solution<Integer> sol) {
        ScQbfSolution scQbfSolution = new ScQbfSolution(sol);
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        if (scQbfSolution.contains(elem)) {
            return 0.0;
        }
        
        Double sum = 0.0;

        Set<Integer> solElements = scQbfSolution.getElements(this.problemInstance.sets);
        
        // Add contribution from interactions with existing elements in solution
        for (Integer e : this.problemInstance.sets.get(elem)) {
            if (!solElements.contains(e)) {
                for (Integer j : solElements) {
                    sum += problemInstance.A[e][j] + problemInstance.A[j][e];
                }
                // Add diagonal element contribution
                sum += problemInstance.A[e][e];
            }
        }    
        
        return sum;
    }

    @Override
    public Double evaluateRemovalCost(Integer elem, Solution<Integer> sol) {
        ScQbfSolution scQbfSolution = new ScQbfSolution(sol);
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        if (!scQbfSolution.contains(elem)) {
            return 0.0;
        }
        
        scQbfSolution.remove(elem);

        return -evaluateInsertionCost(elem, scQbfSolution);
    }

    @Override
    public Double evaluateExchangeCost(Integer elemIn, Integer elemOut, Solution<Integer> sol) {
        ScQbfSolution scQbfSolution = new ScQbfSolution(sol);
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        if (elemIn.equals(elemOut)) {
            return 0.0;
        }
        
        if (scQbfSolution.contains(elemIn)) {
            return evaluateRemovalCost(elemOut, scQbfSolution);
        }
        
        if (!scQbfSolution.contains(elemOut)) {
            return evaluateInsertionCost(elemIn, scQbfSolution);
        }
        
        Double sum = evaluateRemovalCost(elemOut, scQbfSolution);
        
        scQbfSolution.remove(elemOut);

        sum += evaluateInsertionCost(elemIn, scQbfSolution);

        return sum;
    }

    /**
     * Evaluates the coverage of the solution.
     *
     * @param sol
     * @return the proportion of covered elements.
     */
    public double evaluateCoverage(Solution<Integer> sol) {
        ScQbfSolution scQbfSolution = new ScQbfSolution(sol);
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        boolean[] covered = new boolean[problemInstance.domainSize];
        int coveredCount = 0;

        for (Integer idx : scQbfSolution.getElements(this.problemInstance.sets)) {
            if (idx == null) continue;
            if (idx < 0 || idx >= problemInstance.sets.size()) {
                return 0.0; // invalid subset index, return 0 coverage
            }
            if (!covered[idx]) {
                covered[idx] = true;
                coveredCount++;
            }
        }
        
        return (double) coveredCount / problemInstance.domainSize;
    }
    
}
