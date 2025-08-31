package problems.sc_qbf;

import problems.Evaluator;
import solutions.Solution;

public class ScQbfEvaluator implements Evaluator<Integer> {

    public final ScQbfInstance problemInstance;

    public ScQbfEvaluator(ScQbfInstance problemInstance) {
        this.problemInstance = problemInstance;
    }

    public boolean isSolutionValid(Solution<Integer> solution) {
        if (solution == null) return false;
        if (problemInstance.domainSize == 0) return true;

        boolean[] covered = new boolean[problemInstance.domainSize];
        int coveredCount = 0;

        for (Integer idx : solution) {
            if (idx == null) continue;
            if (idx < 0 || idx >= problemInstance.sets.size()) {
                return false; // invalid subset index
            }
            for (Integer elem : problemInstance.sets.get(idx)) {
                if (!covered[elem]) {
                    covered[elem] = true;
                    coveredCount++;
                    if (coveredCount == problemInstance.domainSize) {
                        return true; // early exit if fully covered
                    }
                }
            }
        }

        return coveredCount == problemInstance.domainSize;
    }

    @Override
    public Integer getDomainSize() {
        return problemInstance.domainSize;
    }

    @Override
    public Double evaluate(Solution<Integer> sol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

    @Override
    public Double evaluateInsertionCost(Integer elem, Solution<Integer> sol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluateInsertionCost'");
    }

    @Override
    public Double evaluateRemovalCost(Integer elem, Solution<Integer> sol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluateRemovalCost'");
    }

    @Override
    public Double evaluateExchangeCost(Integer elemIn, Integer elemOut, Solution<Integer> sol) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluateExchangeCost'");
    }
    
}
