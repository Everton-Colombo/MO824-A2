package problems.sc_qbf;

import java.util.ArrayList;
import metaheuristics.grasp.AbstractGRASP;
import solutions.*;

public class GraspScQbf extends AbstractGRASP<Integer> {

    private GraspScQbfConfig config = GraspScQbfConfig.getDefault();

    private ScQbfInstance problemInstance;

    public GraspScQbf(Double alpha, Integer iterations, ScQbfInstance instance) {
        super(new ScQbfEvaluator(instance), alpha, iterations);
        this.problemInstance = instance;
    }

    public GraspScQbf(Double alpha, int iterations, String filePath) {
        super(
            new ScQbfEvaluator(ScQbfInstance.fromFile(filePath)), alpha, iterations
            );
        this.problemInstance = ScQbfInstance.fromFile(filePath);
    }

    @Override
    public ArrayList<Integer> makeCL() {
        ArrayList<Integer> _CL = new ArrayList<>();
        for (int i = 0; i < problemInstance.sets.size(); i++) {
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

        CL = new ArrayList<>();

        for (int i = 0; i < problemInstance.sets.size(); i++) {
            if (!sol.contains(i)) {
                CL.add(i);
            }
        }
    }


    @Override
    public Solution<Integer> createEmptySol() {
        var emptySol = new ScQbfSolution();
        emptySol.cost = 0.0;
        return emptySol;
    }

    @Override
    public Solution<Integer> localSearch() {
        Double maxDeltaCost;
        Integer bestCandIn = null, bestCandOut = null;

        do {
            maxDeltaCost = Double.NEGATIVE_INFINITY;
            updateCL();
            // Generate RCL for insertions
            // How to create RCL here?
            ArrayList<Integer> RCL = makeRCL();
                
            // Evaluate insertions using RCL instead of CL
            for (Integer candIn : RCL) {
                double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
                if (deltaCost > maxDeltaCost) {
                    maxDeltaCost = deltaCost;
                    bestCandIn = candIn;
                    bestCandOut = null;
                }
            }
            // Evaluate removals
            for (Integer candOut : sol) {
                double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
                if (deltaCost > maxDeltaCost) {
                    maxDeltaCost = deltaCost;
                    bestCandIn = null;
                    bestCandOut = candOut;
                }
            }
            // Evaluate exchanges using RCL instead of CL
            for (Integer candIn : RCL) {
                for (Integer candOut : sol) {
                    double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                    if (deltaCost > maxDeltaCost) {
                        maxDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                    }
                }
            }
            // Implement the best move, if it reduces the solution cost.
            if (maxDeltaCost > 0) {
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
        } while (maxDeltaCost > 0);

        return sol;
    }

    @Override
    public Solution<Integer> constructiveHeuristic() {

		CL = makeCL();
		RCL = new ArrayList<>();
		sol = createEmptySol();
		cost = Double.NEGATIVE_INFINITY;

		/* Main loop, which repeats until the stopping criteria is reached. */
		while (!constructiveStopCriteria(cost)) {

			double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
			cost = ObjFunction.evaluate(sol);

			/*
			 * Explore all candidate elements to enter the solution, saving the
			 * highest and lowest cost variation achieved by the candidates.
			 */
			for (Integer c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
				if (deltaCost < minCost)
					minCost = deltaCost;
				if (deltaCost > maxCost)
					maxCost = deltaCost;
			}

			/*
			 * Among all candidates, insert into the RCL those with the highest
			 * performance using parameter alpha as threshold.
			 */
			for (Integer c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, sol);
				if (deltaCost <= minCost + alpha * (maxCost - minCost) && !covers(c)) {
					RCL.add(c);
				}
			}

			/* Choose a candidate randomly from the RCL */
			int rndIndex = rng.nextInt(RCL.size());
			Integer inCand = RCL.get(rndIndex);
			CL.remove(inCand);
			sol.add(inCand);
			cost = ObjFunction.evaluate(sol);
			RCL.clear();
		}

		return sol;
	}

    private boolean covers(Integer set) {
        return ((ScQbfSolution) this.sol).getElements(problemInstance.sets).containsAll(problemInstance.sets.get(set));
    }

    @Override
	public Solution<Integer> solve() {

		bestSol = createEmptySol();
        bestSol.cost = ObjFunction.evaluate(bestSol);
		for (int i = 0; i < iterations; i++) {
			this.sol = constructiveHeuristic();
			this.sol = localSearch();
            sol.cost = ObjFunction.evaluate(sol);
			if (bestSol.cost > sol.cost) {
				bestSol = new Solution<>(sol);
                bestSol.cost = ObjFunction.evaluate(bestSol);
				if (verbose)
					System.out.println("(Iter. " + i + ") BestSol = " + bestSol);
			}
		}

		return bestSol;
	}
}
