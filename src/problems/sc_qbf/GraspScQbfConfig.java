package problems.sc_qbf;

public class GraspScQbfConfig {

    public sealed interface ConstructionMechanism {
        public record Classic(double alpha) implements ConstructionMechanism {}
        public record RandomPlusGreedy(double p) implements ConstructionMechanism {}
    }

    public ConstructionMechanism constructionMechanism;
    
    public static GraspScQbfConfig getDefault() {
        var config = new GraspScQbfConfig();
        config.constructionMechanism = new ConstructionMechanism.Classic(0.5);
        return config;
    }
}
