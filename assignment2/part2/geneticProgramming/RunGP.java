import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.impl.GPGenotype;

public class RunGP {
    public static void main(String[] args) throws InvalidConfigurationException {
        GPProblem problem = new GeneticProblem();
        GPGenotype genotype = problem.create();
        genotype.setVerboseOutput(true);

        int generation = 0;
        while(++generation < 500){
            genotype.evolve(1);
            double fitness = genotype.getFittestProgramComputed().getFitnessValue();
            if(fitness == 0){
                break;
            }
        }
        System.out.println("Best Formula: ");
        genotype.outputSolution(genotype.getAllTimeBest());
    }
}
