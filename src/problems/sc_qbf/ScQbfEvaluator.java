package problems.sc_qbf;

import problems.Evaluator;
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
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        if (sol.contains(elem)) {
            return 0.0;
        }
        
        Double sum = 0.0;
        
        // Add contribution from interactions with existing elements in solution
        for (Integer j : sol) {
            sum += problemInstance.A[elem][j] + problemInstance.A[j][elem];
        }
        
        // Add diagonal element contribution
        sum += problemInstance.A[elem][elem];
        
        return sum;
    }

    @Override
    public Double evaluateRemovalCost(Integer elem, Solution<Integer> sol) {
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        if (!sol.contains(elem)) {
            return 0.0;
        }
        
        Double sum = 0.0;
        
        // Calculate negative contribution from interactions with other elements in solution
        for (Integer j : sol) {
            if (!j.equals(elem)) {
                sum += problemInstance.A[elem][j] + problemInstance.A[j][elem];
            }
        }
        
        // Add diagonal element contribution
        sum += problemInstance.A[elem][elem];
        
        return -sum;
    }

    @Override
    public Double evaluateExchangeCost(Integer elemIn, Integer elemOut, Solution<Integer> sol) {
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        if (elemIn.equals(elemOut)) {
            return 0.0;
        }
        
        if (sol.contains(elemIn)) {
            return evaluateRemovalCost(elemOut, sol);
        }
        
        if (!sol.contains(elemOut)) {
            return evaluateInsertionCost(elemIn, sol);
        }
        
        Double sum = 0.0;
        
        // Add contribution from inserting elemIn
        for (Integer j : sol) {
            if (!j.equals(elemOut)) {
                sum += problemInstance.A[elemIn][j] + problemInstance.A[j][elemIn];
            }
        }
        sum += problemInstance.A[elemIn][elemIn];
        
        // Subtract contribution from removing elemOut
        for (Integer j : sol) {
            if (!j.equals(elemOut)) {
                sum -= problemInstance.A[elemOut][j] + problemInstance.A[j][elemOut];
            }
        }
        sum -= problemInstance.A[elemOut][elemOut];
        
        // Subtract interaction between elemIn and elemOut
        sum -= (problemInstance.A[elemIn][elemOut] + problemInstance.A[elemOut][elemIn]);
        
        return sum;
    }

    /**
     * Evaluates the coverage of the solution.
     *
     * @param sol
     * @return the proportion of covered elements.
     */
    public double evaluateCoverage(Solution<Integer> sol) {
        if (this.problemInstance == null)
            throw new IllegalStateException("Problem instance is not initialized");

        boolean[] covered = new boolean[problemInstance.domainSize];
        int coveredCount = 0;

        for (Integer idx : sol) {
            if (idx == null) continue;
            if (idx < 0 || idx >= problemInstance.sets.size()) {
                return 0.0; // invalid subset index, return 0 coverage
            }
            for (Integer elem : problemInstance.sets.get(idx)) {
                if (!covered[elem]) {
                    covered[elem] = true;
                    coveredCount++;
                }
            }
        }
        
        return (double) coveredCount / problemInstance.domainSize;
    }
    
}
