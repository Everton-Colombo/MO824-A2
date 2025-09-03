package solutions;

import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class ScQbfSolution extends Solution<Integer> {
		
	public ScQbfSolution() {
		super();
        this.cost = Double.NEGATIVE_INFINITY;
	}
	
	public ScQbfSolution(Solution<Integer> sol) {
		super(sol);
		cost = sol.cost;
	}

    public Set<Integer> getElements(List<Set<Integer>> sets) {
        Set<Integer> elements = Set.of();
        for (Integer i : this) {
			if (i == null) {
				continue;
			}
			if (i >= sets.size()) {
				continue;
			}
            elements.addAll(sets.get(i));
        }
        return elements;
    }

	@Override
	public String toString() {
		return "Solution: cost=[" + cost + "], size=[" + this.size() + "], elements=" + super.toString();
	}

}

