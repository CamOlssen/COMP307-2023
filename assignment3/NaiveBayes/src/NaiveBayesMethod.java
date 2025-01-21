import java.io.*;
import java.util.*;
public class NaiveBayesMethod {
    private List<Instance> trainingInstances = new ArrayList<>();
    private List<String> attributeNames = new ArrayList<>();
    private List<List<String>> attributeValues = new ArrayList<>();
    private HashSet<String> labels = new HashSet<>();
    private List<HashMap<String,Integer>> counts = new ArrayList<>();
    private HashMap<String,Integer> countY = new HashMap<>();
    private HashMap<String,Integer> totalCount = new HashMap<>();
    private HashMap<String,Double> probTable = new HashMap<>();
    private HashMap<String, Double> probTableY = new HashMap<>();
    private int total = 0;

    public NaiveBayesMethod(String training, String testing){
        File trainingFile = new File(training);
        try{
            Scanner scan = new Scanner(trainingFile);
            String attributeString = scan.next(); //split the first line of the training file for our attribute names
            String[] attributes = attributeString.split(",");
            for(int i = 2; i<attributes.length; i++){
                attributeNames.add(attributes[i]);
            }
            System.out.println("Attributes: "+attributeNames.toString());
            for(int i = 0; i < attributeNames.size(); i++) counts.add(new HashMap<>());
            //initialise the possible values for our data
            //this is a giant mess of code and i should really put it in a method or something for cleanness
            ArrayList<String> ages = new ArrayList<>(Arrays.asList("10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90-99"));
            ArrayList<String> menopauses = new ArrayList<>(Arrays.asList("lt40", "ge40", "premeno"));
            ArrayList<String> tumorsizes = new ArrayList<>(Arrays.asList("0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44", "45-49", "50-54", "55-59"));
            ArrayList<String> invNodes = new ArrayList<>(Arrays.asList("0-2", "3-5", "6-8", "9-11", "12-14", "15-17", "18-20", "21-23", "24-26", "27-29", "30-32", "33-35", "36-39"));
            ArrayList<String> nodeCaps = new ArrayList<>(Arrays.asList("yes", "no"));
            ArrayList<String> degmalig = new ArrayList<>(Arrays.asList("1", "2", "3"));
            ArrayList<String> breast = new ArrayList<>(Arrays.asList("left", "right"));
            ArrayList<String> breastquad = new ArrayList<>(Arrays.asList("left_up", "left_low", "right_up", "right_low", "central"));
            ArrayList<String> irradiat = new ArrayList<>(Arrays.asList("yes", "no"));
            attributeValues.add(ages);
            attributeValues.add(menopauses);
            attributeValues.add(tumorsizes);
            attributeValues.add(invNodes);
            attributeValues.add(nodeCaps);
            attributeValues.add(degmalig);
            attributeValues.add(breast);
            attributeValues.add(breastquad);
            attributeValues.add(irradiat);

            while(scan.hasNext()){
                String line = scan.next();
                String[] splitLine = line.split(",");
                labels.add(splitLine[1]);
                trainingInstances.add(new Instance(splitLine));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Distinct Labels: "+labels.toString());
        for(int i = 0; i< attributeNames.size();i++){
            System.out.println(attributeNames.get(i)+": "+attributeValues.get(i).toString());
        }
        System.out.println("File read, beginning training.");

        //train the algorithm
        this.train();
        System.out.println("Training complete, beginning testing");

        //testing time
        File testFile = new File(testing);
        List<Instance> testInstances = new ArrayList<>();
        try{
            Scanner scan = new Scanner(testFile);
            scan.next(); //skips attribute names
            while(scan.hasNext()){
                String line = scan.next();
                String[] splitLine = line.split(",");
                testInstances.add(new Instance(splitLine));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Performing predictions:");
        this.makePredictions(testInstances);
    }

    public void train(){
        //divided into individual methods because this was an unreadable nightmare block otherwise
        //broadly, gets the counts set correctly for training data, then gets probabilities
        this.initCount();
        this.updateCount();
        this.updateTotal();
        this.getConditionalProbs();
    }

    public void makePredictions(List<Instance> testInstances){
        List<String> testLabels = new ArrayList<>();
        List<String> predictedLabels = new ArrayList<>();
        for(Instance instance : testInstances)testLabels.add(instance.getInstanceLabel());

        int index = 1;
        for(Instance instance : testInstances){
            double finalScore = 0.0;
            String finalLabel = "";

            for(String label : labels){
                double score = classScore(instance, label);
                System.out.println("Instance "+index+" "+label+" score:"+score);
                if(score > finalScore){
                    finalScore = score;
                    finalLabel = label;
                }
            }
            System.out.println("Final label prediction: "+finalLabel+" | Correct label: "+instance.getInstanceLabel()+"\n");
            predictedLabels.add(finalLabel);
            index++;
        }
        System.out.println(testLabels);
        System.out.println(predictedLabels);
        int correct = 0;
        for(int i = 0;i< testLabels.size();i++){
            if(testLabels.get(i).equals(predictedLabels.get(i))) correct++;
        }
        double accuracy = (double)correct/(double)testLabels.size();
        System.out.println("Accuracy = "+accuracy+", with "+correct+" of "+testLabels.size()+" correct predictions.");
    }

    public double classScore(Instance instance, String label){
        double score = probTableY.get(label);
        List<String> vals = instance.getValues();

        for(int i = 0; i<vals.size();i++){
            String attributeName = attributeNames.get(i);
            String attributeValue = vals.get(i);
            String probKey = attributeName+"="+attributeValue+"|class="+label;
            double probability = probTable.get(probKey);
            score = score * probability;
        }
        return score;
    }
    public void initCount(){
        for(String label : labels){
            countY.put(label, 1);
        }
        for(int i = 0;i<attributeValues.size();i++){
            String attributeName = attributeNames.get(i);
            List<String> Xi = attributeValues.get(i);
            HashMap<String, Integer> record = counts.get(i);
            for(String value : Xi){
                for(String label : labels){
                    String key = attributeName+"="+value+",class="+label;
                    record.put(key, 1);
                }
            }
        }
    }
    public void updateCount(){
        for(Instance instance : trainingInstances){
            String label = instance.getInstanceLabel();
            countY.put(label, countY.get(label)+1);
            List<String> vals = instance.getValues();
            for(int i=0;i<vals.size();i++){
                String attributeName = attributeNames.get(i);
                HashMap<String, Integer> currentCount = counts.get(i);
                String key = attributeName+"="+vals.get(i)+",class="+label;
                currentCount.put(key, currentCount.get(key)+1);
            }
        }
    }

    public void updateTotal(){
        for(String label : labels){
            total = total+countY.get(label);
            for(int i=0;i< attributeNames.size();i++){
                String attribute = attributeNames.get(i);
                HashMap<String, Integer> hashMap = counts.get(i);
                int count = 0;
                for(Map.Entry<String, Integer> entry : hashMap.entrySet()){
                    String[] split = entry.getKey().split(",");
                    if(split[1].equals("class="+label)){
                        count = count+entry.getValue();
                    }
                }
                String key = attribute+",class="+label;
                totalCount.put(key, count);
            }
        }
    }

    public void getConditionalProbs(){
        for(String label : labels){
            double probY = (double)countY.get(label)/(double)total;
            probTableY.put(label,probY);

            for(int i=0;i<attributeNames.size();i++){
                String attributeName = attributeNames.get(i);
                for(String xi : attributeValues.get(i)){
                    String countKey = attributeName+"="+xi+",class="+label;
                    String totalKey = attributeName+",class="+label;
                    String probKey = attributeName+"="+xi+"|class="+label;

                    int count = counts.get(i).get(countKey);
                    int total2 = totalCount.get(totalKey);
                    double probability = (double)count/(double)total2;
                    probTable.put(probKey, probability);
                }
            }
        }
        //probability printing for report,commented out for legibility
        System.out.println("Y probability");
        for(Map.Entry<String, Double> entry : probTableY.entrySet()){
            System.out.println(entry.toString());
        }
        System.out.println("\n");

        System.out.println("X probability");
        for(Map.Entry<String, Double> entry : probTable.entrySet()){
            System.out.println(entry.toString());
        }
        System.out.println("\n");
    }

    public static void main(String[] args) {
        if(args.length == 2) {
            String trainingFile = args[0];
            String testingFile = args[1];
            new NaiveBayesMethod(trainingFile, testingFile);
        }
        else{
            new NaiveBayesMethod("breast-cancer-training.csv", "breast-cancer-test.csv");
        }
    }
}