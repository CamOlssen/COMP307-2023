import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class DecisionTree {

    private ArrayList<Instance> instances;
    private ArrayList<Instance> testInstances;
    private ArrayList<String> attributeNames;
    private ArrayList<String> categoryNames;
    private Node root;
    private String trainingFile;
    private String testingFile;
    private String majorityCat;
    private double mostFrequent;

    public DecisionTree(String trainingFile, String testingFile) {

        this.trainingFile = trainingFile;
        this.testingFile = testingFile;

        this.init(trainingFile, testingFile);

        System.out.println("Results from training on "+trainingFile+ " and testing on "+testingFile);
        this.performDecisionTree();

        this.printTree("", root);
    }

    public void init(String train_file_name, String test_file_name) {

        DataReader reader = new DataReader();
        reader.readDataFile(train_file_name);

        instances = (ArrayList<Instance>) reader.getAllInstances();
        attributeNames = (ArrayList<String>) reader.getAttNames();

        HashSet<String> a =  (HashSet<String>) reader.getCategoryNames();
        categoryNames = new ArrayList<String>(a);
        ArrayList<String> copy = new ArrayList<>();
        copy.addAll(attributeNames);

        Map.Entry<String, Integer> entry = majorityCheck(instances);

        majorityCat = entry.getKey();

        mostFrequent = (double)entry.getValue() / (double)instances.size();

        root = constructDecisionTree(instances, copy);

        DataReader reader1 = new DataReader();
        reader1.readDataFile(test_file_name);

        testInstances = (ArrayList<Instance>) reader1.getAllInstances();

    }

    public Node constructDecisionTree(ArrayList<Instance> instances, ArrayList<String> attributes) {

        String best = "";
        HashSet<String> classes = new HashSet<>();

        Node left;
        Node right;

        for(Instance instance: instances) {
            classes.add(instance.getCategory());
        }

        if(instances.isEmpty()) {
            return new Node(majorityCat, mostFrequent);
        }

        else if(classes.size() == 1) {
            return new Node(instances.get(0).getCategory(), 1);
        }

        else if(attributes.isEmpty()) {
            Map.Entry<String, Integer> entry = majorityCheck(instances);
            String majority_category_2 = entry.getKey();
            double prob_majority = (double)entry.getValue() / (double)instances.size();

            return new Node(majority_category_2, prob_majority);
        }

        else {
            double impurityLowest = 100;

            ArrayList<Instance> bestInstTrue = new ArrayList<>();
            ArrayList<Instance> bestInstFalse = new ArrayList<>();

            for(String attribute: attributes) {

                ArrayList<Instance> trueSet = new ArrayList<>();
                ArrayList<Instance> falseSet = new ArrayList<>();

                for(Instance ins: instances) {
                    if(ins.getAtt(attributeNames.indexOf(attribute)) == true) {
                        trueSet.add(ins);
                    }
                    else {
                        falseSet.add(ins);
                    }
                }
                double impurityTrue = calculateImpurity(trueSet);
                impurityTrue = impurityTrue * (double)trueSet.size() / (double)instances.size();

                double impurityFalse = calculateImpurity(falseSet);
                impurityFalse = impurityFalse * (double)falseSet.size()/(double)instances.size();

                double impurityAverage = impurityTrue + impurityFalse;

                if(impurityAverage < impurityLowest) {

                    impurityLowest = impurityAverage;
                    best = attribute;

                    bestInstTrue.clear();
                    bestInstTrue.addAll(trueSet);

                    bestInstFalse.clear();
                    bestInstFalse.addAll(falseSet);
                }

            }

            attributes.remove(best);

            ArrayList<String> copy = new ArrayList<>();
            copy.addAll(attributes);

            ArrayList<String> copy2 = new ArrayList<>();
            copy2.addAll(attributes);

            left  = constructDecisionTree(bestInstTrue, copy);
            right = constructDecisionTree(bestInstFalse, copy2);
        }

        return new Node(best, left, right);
    }


    public String predict(Instance instance) {
        Node current = root;

        while(true) {
            if(current.isLeaf()) return current.getNodeClass();

            String bestAttributeCurrentNode = current.getBest();
            int attributeIndex = attributeNames.indexOf(bestAttributeCurrentNode);
            boolean val = instance.getAtt(attributeIndex);

            if(val) current = current.getLeft();
            else current = current.getRight();
        }
    }

    public void printTree(String indent, Node node) {
        if(!node.isLeaf()) {
            System.out.println(indent + node.getBest() + " = True:");
            printTree(indent+"   ", node.getLeft());
            System.out.println(indent + node.getBest() + " = False:");
            printTree(indent+"   ", node.getRight());
        }
        else {
            System.out.println(indent + "Class " + node.getNodeClass() + ", prob = " + node.getProb());
        }
    }

    public double performDecisionTree() {
        ArrayList<String> classReal = new ArrayList<>();
        ArrayList<String> classPredicted = new ArrayList<>();

        for(Instance i: testInstances) {
            classReal.add(i.getCategory());
            classPredicted.add(predict(i));
        }

        int correct = 0;
        for(int i=0; i<classReal.size(); i++) {
            if(classReal.get(i).equals(classPredicted.get(i))) {
                correct ++;
            }
        }

        double accuracy = (double)correct / (double)classReal.size();

        System.out.printf("Accuracy: %.3f with " + correct + " correct predictions\n", accuracy);

        return accuracy;
    }



    public void performKFold() {

        double average = 0.0;

        System.out.println("\nResult from 10-Fold Cross Validation hepatitis:");

        for(int i=0; i<10; i++) {
            System.out.print("Fold " + (i+1) + " ");
            String train = trainingFile + "-run-" + i;
            String test  = testingFile + "-run-" + i;


            this.init(train, test);
            double accuracy = this.performDecisionTree();
            average = average + accuracy;

        }

        average = average / 10;

        System.out.println("Average accuracy: " + average);
    }


    //helper methods
    public Map.Entry<String, Integer> majorityCheck(ArrayList<Instance> instances){
        HashMap<String, Integer> catOccurance = new HashMap<>();
        for(Instance instance: instances) {
            String catego = instance.getCategory();

            if(!catOccurance.keySet().contains(catego)) {
                catOccurance.put(catego, 1);
            }

            else {
                catOccurance.put(catego, catOccurance.get(catego)+1);
            }
        }

        int mostOccured = 0;
        String mostOccuredCat = "";

        for(String key: catOccurance.keySet()) {
            int val = catOccurance.get(key);

            if(val > mostOccured) {
                mostOccured = val;
                mostOccuredCat = key;
            }
        }

        for(Map.Entry<String, Integer> entry: catOccurance.entrySet()) {
            if(entry.getKey().equals(mostOccuredCat)) {
                return entry;
            }
        }

        return null;
    }

    public double calculateImpurity(ArrayList<Instance> instances) {

        if(instances.isEmpty()) {
            return 0.0;
        }

        double impurity = 0.0;
        int cate1_count = 0;
        int cate2_count = 0;

        for(Instance instance: instances) {
            if(instance.getCategory().equals(categoryNames.get(0))) {
                cate1_count ++;
            }
            else if(instance.getCategory().equals(categoryNames.get(1))){
                cate2_count ++;
            }

        }

        impurity = (double)(cate1_count*cate2_count)/(Math.pow((double)instances.size(),2));

        return impurity;
    }

    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Run again with correct number of arguments!");
        }
        else {
            String testingFile = args[0];
            String trainingFile  = args[1];

            new DecisionTree(testingFile, trainingFile);
        }
    }
}
