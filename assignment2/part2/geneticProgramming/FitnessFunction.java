import java.util.*;
import org.jgap.gp.*;
import org.jgap.gp.terminal.Variable;
//Fitness function based on tutorial at https://cvalcarcel.wordpress.com/2009/08/04/jgap-a-firstsimple-tutorial/
public class FitnessFunction extends GPFitnessFunction {
    List<Double> xList;
    List<Double> yList;
    Variable xVar;
    Variable yVar;

    public FitnessFunction(List<Double> xList, List<Double> yList, Variable x, Variable y){
        this.xList = xList;
        this.yList = yList;
        this.xVar = x;
        this.yVar = y;
    }
    @Override
    protected double evaluate(IGPProgram igpProgram) {
        double result = 0.0f;
        long longresult = 0;
        for(int i = 0; i < xList.size(); i++){
            xVar.set(xList.get(i)); //we only do xVar here as y is not input in this assignment
            long value = (long)igpProgram.execute_double(0, new Object[0]);
            longresult += Math.abs(value - yList.get(i));
        }
        result = longresult;
        return result;
    }
}
