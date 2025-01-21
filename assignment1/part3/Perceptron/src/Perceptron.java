import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
public class Perceptron {
    private File data;
    private int attCount = 0;
    private ArrayList<Instance> instances = new ArrayList<>();
    private double[] bestWeights;

    public Perceptron(String file_name){
        data  = new File(file_name);
        try {
            Scanner scan = new Scanner(data);
            Scanner s = new Scanner(scan.nextLine());

            while (s.hasNext()) {
                s.next();
                attCount++;
            }

            attCount = attCount - 1;
            while(scan.hasNextDouble()) {
                Instance instance = new Instance();
                for(int i=0; i<attCount; i++) {
                    double val = scan.nextDouble();
                    instance.addVal(val);
                }
                String cat = scan.next();
                instance.setCategory(cat);
                instances.add(instance);
            }

            scan.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        //initialise weights as 0.5
        bestWeights = new double[attCount+1];

        for(int i=0; i<bestWeights.length; i++) {
            bestWeights[i] = 0.5;
        }

        ArrayList<Instance> train_set;
        ArrayList<Instance> test_set;

        System.out.println("Perceptron: training and testing the same data.\n ");
        this.adjustWeights(instances);
        this.predict(instances);
        System.out.print("Final set of weights: ");
        for(int i=0; i<bestWeights.length; i++) {
            System.out.printf("%.2f,", bestWeights[i]);
        }
        System.out.println();
        System.out.println("\n ");

        System.out.println("Perceptron: training and testing different data.\n ");
        int port = instances.size() / 100 * 90;
        Collections.shuffle(instances);
        train_set = new ArrayList<> (instances.subList(0, port));
        test_set = new ArrayList<> (instances.subList(port, instances.size()));
        this.adjustWeights(train_set);
        this.predict(test_set);
        System.out.println("\n ");
    }

    public void adjustWeights(ArrayList<Instance> training){
        double[] weights = new double[bestWeights.length];
        for(int i = 0; i < weights.length; i++) weights[i] = 0.5;

        int iteration = 0;
        boolean stoptrain = false;
        int bestIteration = 0;
        double bestAccuracy = 0;
        int bestCorrect = 0;

        while(iteration < 500 && stoptrain == false) {

            for(Instance ins: training) {

                double prediction = 1;
                double weighted_sum = getWeightedSum(ins.getValues(), weights);
                if(weighted_sum < 0) {
                    prediction = 0;
                }
                if(prediction == 1 && ins.getCategory() == 0) {
                    for(int i=0; i<weights.length; i++) {
                        weights[i] = weights[i] - ins.getAttributeIndex(i);
                    }

                }
                else if(prediction == 0 && ins.getCategory() == 1) {
                    for(int i=0; i<weights.length; i++) {
                        weights[i] = weights[i] + ins.getAttributeIndex(i);
                    }
                }
            }

            double accuracy = getAccuracy(training, weights);
            double correct = accuracy*training.size();

            if(accuracy == 1.0) {
                System.out.println("Perceptron reaches 100% accuracy after " + iteration + " iterations.");
                stoptrain = true; //100% accuract reached, no need to keep training
            }

            if(accuracy > bestAccuracy) {
                bestCorrect = (int)correct;
                bestIteration = iteration;
                bestAccuracy = accuracy;
                for(int i=0; i<weights.length; i++) {
                    bestWeights[i] = weights[i];
                }
            }
            iteration ++;
        }
        System.out.println(bestCorrect+" correct guesses out of "+training.size()+" instances.\n"+(training.size() - bestCorrect)+" incorrect guesses.");
        System.out.println("Accuracy: " +bestAccuracy+ " after "+bestIteration+" iterations.");
    }

    public double predict(ArrayList<Instance> test){
        double accuracy = getAccuracy(test, bestWeights);
        System.out.println("Accuracy on test set: "+accuracy);
        return accuracy;
    }

    public double getAccuracy(ArrayList<Instance> instances, double[] weights){
        int correct = 0;
        for(Instance i : instances){
            int categoryi = i.getCategory();
            int predictioni = 0;
            double weightedSum = getWeightedSum(i.getValues(), weights);
            if(weightedSum > 0) predictioni = 1;
            if(categoryi == predictioni) correct++;
        }
        return (double)correct/(double)instances.size();
    }
    public double getWeightedSum(ArrayList<Double> features, double[] weights){
        double weightedSum = 0;
        for(int i = 0; i < features.size(); i++) weightedSum = weightedSum + features.get(i)*weights[i];
        return weightedSum;
    }

    public static void main(String[] args) {
        if(args.length != 1) System.out.println("Run again with correct number of arguments!");
        else new Perceptron(args[0]);
    }
}