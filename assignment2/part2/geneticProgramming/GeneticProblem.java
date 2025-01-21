import java.io.*;
import java.util.*;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.*;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Constant;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

public class GeneticProblem extends GPProblem{
    List<Double> xList = new ArrayList<>();
    List<Double> yList = new ArrayList<>();
    private Variable xVar;
    private Variable yVar;
    private Constant R;

    public GeneticProblem() throws InvalidConfigurationException{
        super((new GPConfiguration()));
        FileRead("regression.txt");

        GPConfiguration configuration = getGPConfiguration();
        this.xVar = Variable.create(configuration, "X", CommandGene.DoubleClass);
        this.yVar = Variable.create(configuration, "Y", CommandGene.DoubleClass);

        configuration.setFitnessEvaluator(new DeltaGPFitnessEvaluator());
        configuration.setMaxInitDepth(4);
        configuration.setPopulationSize(1000);
        configuration.setMaxCrossoverDepth(8);
        configuration.setReproductionProb((float)0.015);
        configuration.setMutationProb((float)0.015);

        configuration.setFitnessFunction(new FitnessFunction(this.xList, this.yList, this.xVar, this.yVar));
        configuration.setStrictProgramCreation(true);

        this.R = new Constant(configuration, CommandGene.DoubleClass, new Random().nextDouble());
    }
    @Override
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration configuration = getGPConfiguration();

        Class[] types = { CommandGene.DoubleClass };
        Class[][] argTypes = { {} };
        CommandGene[][] nodeSets = {
                {
                        xVar,
                        //yVar,
                        new Terminal(configuration, CommandGene.DoubleClass, 0.0, 10.0, true),
                        new Add(configuration, CommandGene.DoubleClass),
                        new Multiply(configuration, CommandGene.DoubleClass),
                        new Subtract(configuration, CommandGene.DoubleClass),
                        new Divide(configuration, CommandGene.DoubleClass)
                }
        };

        GPGenotype result = GPGenotype.randomInitialGenotype(configuration, types, argTypes,
                nodeSets, 20, true);

        return result;
    }

    public void FileRead(String file){
        try{
            Scanner scan = new Scanner(new File(file));
            scan.nextLine(); //first two lines are just headers, skip over them
            scan.nextLine();
            while(scan.hasNext()){
                String line = scan.nextLine();
                Scanner lineScan = new Scanner(line);
                int i = -1;
                while(lineScan.hasNextDouble()){
                    i++;
                    double value = lineScan.nextDouble();
                    if(i == 0) xList.add(value);
                    else if(i == 1) yList.add(value);
                }
                lineScan.close();
            }
            scan.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
