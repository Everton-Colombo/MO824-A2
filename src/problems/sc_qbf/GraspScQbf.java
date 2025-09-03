package problems.sc_qbf;

import java.util.ArrayList;

import metaheuristics.grasp.AbstractGRASP;
import solutions.Solution;
>
public class GraspScQbf extends AbstractGRASP<Integer> {

    private GraspScQbfConfig config = GraspScQbfConfig.getDefault();

    private ScQbfInstance problemInstance;

    public GraspScQbf(Double alpha, Integer iterations, ScQbfInstance instance) {
        super(new ScQbfEvaluator(instance), alpha, iterations);
        this.problemInstance = instance;
    }

    public GraspScQbf(Double alpha, int iterations, String filePath) {
        super(new ScQbfEvaluator(this.problemInstance = ScQbfInstance.fromFile(filePath)), alpha, iterations);
    }

    @Override
    public ArrayList<Integer> makeCL() {
        ArrayList<Integer> _CL = new ArrayList<>();
        for (int i = 0; i < problemInstance.domainSize; i++) {
            _CL.add(i);
        }
        return _CL;
    }

    @Override
    public ArrayList<Integer> makeRCL() {
        ArrayList<Integer> _RCL = new ArrayList<>();

        // Obtain subset of CL that contains only elements that will increase coverage
        ScQbfEvaluator evaluator = (ScQbfEvaluator) ObjFunction;
        for (Integer elem : CL) {
            Solution<Integer> tempSol = new Solution<>(sol);
            tempSol.add(elem);
            
            // Check if adding this element increases coverage
            double currentCoverage = evaluator.evaluateCoverage(sol);
            double newCoverage = evaluator.evaluateCoverage(tempSol);

            if (newCoverage > currentCoverage) {
                _RCL.add(elem);
            }
        }
        
        return _RCL;
    }

    @Override
    public void updateCL() {
        // Candidate List: All elements not in the current solution

        CL = new ArrayList<Integer>();

        for (int i = 0; i < problemInstance.domainSize; i++) {
            if (!sol.contains(i)) {
                CL.add(i);
            }
        }
    }


    @Override
    public Solution<Integer> createEmptySol() {
        var emptySol = new Solution<Integer>();
        emptySol.cost = 0.0;
        return emptySol;
    }

    @Override
    public Solution<Integer> localSearch() {
        Double minDeltaCost;
        Integer bestCandIn = null, bestCandOut = null;

        do {
            minDeltaCost = Double.POSITIVE_INFINITY;
            updateCL();
            // Generate RCL for insertions
            ArrayList<Integer> RCL = makeRCL();
                
            // Evaluate insertions using RCL instead of CL
            for (Integer candIn : RCL) {
                double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandIn = candIn;
                    bestCandOut = null;
                }
            }
            // Evaluate removals
            for (Integer candOut : sol) {
                double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
                if (deltaCost < minDeltaCost) {
                    minDeltaCost = deltaCost;
                    bestCandIn = null;
                    bestCandOut = candOut;
                }
            }
            // Evaluate exchanges using RCL instead of CL
            for (Integer candIn : RCL) {
                for (Integer candOut : sol) {
                    double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                    if (deltaCost < minDeltaCost) {
                        minDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                    }
                }
            }
            // Implement the best move, if it reduces the solution cost.
            if (minDeltaCost < -Double.MIN_VALUE) {
                if (bestCandOut != null) {
                    sol.remove(bestCandOut);
                    CL.add(bestCandOut);
                }
                if (bestCandIn != null) {
                    sol.add(bestCandIn);
                    CL.remove(bestCandIn);
                }
                ObjFunction.evaluate(sol);
            }
        } while (minDeltaCost < -Double.MIN_VALUE);

        return sol;
    }

}
