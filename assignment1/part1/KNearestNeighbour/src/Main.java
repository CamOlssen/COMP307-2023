import java.io.*;
import java.util.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
            String training = args[0];
            String test = args[1];

            //set k-value to 1 by default
            int k = 1;
            if(args.length == 3){
                k = Integer.parseInt(args[2]);
            }

            ArrayList<double[]> trainingData = fileRead(training); //parse file
            ArrayList<double[]> testData = fileRead(test); //ditto
            assert trainingData != null;
            assert testData != null;

            double[] ranges = rangeFind(trainingData);
            double accuracy = 0;

            int i = 0;
            double j = 0;
            for(double[] testRow : testData){
                int guess = KNNSearch(k, testRow, trainingData, ranges);
                System.out.printf("Instance %d: \t Label : %d | Prediction: %d\n", i, (int)testRow[13], guess);
                i++;
                if (guess == testRow[13]) j++;
            }
            accuracy = (j)/testData.size()*100;

        System.out.printf("\n-------------------\n" +
                "K = %d\n" +
                "Correct Predictions: %d\n" +
                "Total Classes: %d\n" +
                "Accuracy: %f%%", k, (int)j, testData.size(), accuracy);
        }

    private static int KNNSearch(int k, double[] test, ArrayList<double[]> training, double[] ranges){
        Tuple[] neighbours = new Tuple[k];

        for(double[] trainingRow : training){
            double dist = getEuclideanDistance(test, trainingRow, ranges);

            for(int i = 0; i < k; i++){
                if(neighbours[i] == null || dist < neighbours[i].getDist()){
                    neighbours[i] = new Tuple(dist, trainingRow[13]);
                    break;
                }
            }
        }
        return getLabel(neighbours);
    }
    private static int getLabel(Tuple[] neighbours){
        int[] count = {0,0,0};
        int guess = 0;

        for(Tuple neighbour : neighbours){
            switch((int) (neighbour.getLabel())){
                case 1:
                    count[0]++;
                    break;
                case 2:
                    count[1]++;
                    break;
                case 3:
                    count[2]++;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value "+ (int) neighbour.getLabel());
            }
        }
        if(count[0] > count[1] && count[0] > count[2]) guess = 1;
        else if(count[1] > count[0] && count[1] > count[2]) guess = 2;
        else if(count[2] > count[0] && count[2] > count[1]) guess = 3;
        return guess;
    }
    private static ArrayList<double[]> fileRead (String file){
        FileInputStream input = null;
        try{
            input = new FileInputStream(file);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        assert input != null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        try{
            ArrayList<double[]> data = new ArrayList<>();
            reader.readLine();
            while((line = reader.readLine()) != null){
                String[] row = line.split(" ");
                double[] rowdouble = new double[row.length];
                for(int i = 0; i < row.length; i++){
                    rowdouble[i] = Double.parseDouble(row[i]);
                }
                data.add(rowdouble);
            }
            return data;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try{
        reader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }
    private static double[] rangeFind (ArrayList<double[]> trainingData){
        double[] ranges = new double[trainingData.get(0).length - 1];
        for(int i = 0; i < ranges.length; i++) {
            double max = 0;
            double min = 0;
            for(double[] training : trainingData){
                double d = training[i];
                if (d > max) max = d;
                else if (d < min) min = d;
            }
            ranges[i] = max-min+1;
        }
        return ranges;
    }

    private static double getEuclideanDistance(double[] test, double[] training, double[] ranges){
        assert test.length == training.length;
        double dist = 0.0;
        for(int i = 0; i < test.length - 1; i++){
            dist += Math.pow(test[i] - training[i], 2) / Math.pow(ranges[i], 2);
        }
        return Math.sqrt(dist);
    }
}

