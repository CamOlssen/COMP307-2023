import java.util.Arrays;

public class NeuralNetwork {
    public final double[][] hidden_layer_weights;
    public final double[][] output_layer_weights;
    private final int num_inputs;
    private final int num_hidden;
    private final int num_outputs;
    private final double learning_rate;

    //add bias
    private double[] hiddenBias = new double[]{-0.02, -0.20};
    private double[] outputBias = new double[]{-0.33, 0.26, 0.06};

    public NeuralNetwork(int num_inputs, int num_hidden, int num_outputs, double[][] initial_hidden_layer_weights, double[][] initial_output_layer_weights, double learning_rate) {
        //Initialise the network
        this.num_inputs = num_inputs;
        this.num_hidden = num_hidden;
        this.num_outputs = num_outputs;

        this.hidden_layer_weights = initial_hidden_layer_weights;
        this.output_layer_weights = initial_output_layer_weights;

        this.learning_rate = learning_rate;
    }


    //Calculate neuron activation for an input
    public double sigmoid(double input) {
        double output = 1 / (1 + Math.exp(-input));
        return output;
    }

    //Feed forward pass input to a network output
    public double[][] forward_pass(double[] inputs) {
        //hidden layer
        double[] hidden_layer_outputs = new double[num_hidden];
        for (int i = 0; i < num_hidden; i++) {
            double output = 0;
            int inputNodeIndex = -1;
            for (double[] input_node_weights : this.hidden_layer_weights) {
                double weighted = input_node_weights[i];
                output += weighted * inputs[++inputNodeIndex];
            }
            output += hiddenBias[i];
            hidden_layer_outputs[i] = sigmoid(output);
        }

        //output layer
        double[] output_layer_outputs = new double[num_outputs];
        for (int i = 0; i < num_outputs; i++) {
            double output = 0;
            int hiddenNodeIndex = -1;
            for (double[] hidden_node_weights : this.output_layer_weights) {
                double weighted = hidden_node_weights[i];
                output += weighted * hidden_layer_outputs[++hiddenNodeIndex];
            }
            output += outputBias[i];
            output_layer_outputs[i] = sigmoid(output);
        }
        return new double[][] { hidden_layer_outputs, output_layer_outputs };
    }

    public double[][][] backward_propagate_error(double[] inputs, double[] hidden_layer_outputs,
                                                 double[] output_layer_outputs, int desired_outputs) {

        double[] output_layer_betas = new double[num_outputs];
        //Calculate output layer betas.
        int[] desired_outputs_array = new int[3];
        if(desired_outputs == 0) desired_outputs_array = new int[] {1, 0, 0};
        else if (desired_outputs == 1) desired_outputs_array = new int[] {0, 1, 0};
        else if (desired_outputs == 2) desired_outputs_array = new int[] {0, 0, 1};

        for(int i = 0; i < output_layer_outputs.length; i++){
            double beta = desired_outputs_array[i] - output_layer_outputs[i];
            output_layer_betas[i] = beta;
        }
        System.out.println("OL betas: " + Arrays.toString(output_layer_betas));

        double[] hidden_layer_betas = new double[num_hidden];
        //Calculate hidden layer betas.
        for (int i = 0; i < hidden_layer_outputs.length; i++) {
            double beta = 0;
            for (int j = 0; j < output_layer_outputs.length; j++) {
                double outputLayerNode_output = output_layer_outputs[j];
                double slope = outputLayerNode_output * (1 - outputLayerNode_output);
                beta += output_layer_weights[i][j] * slope * output_layer_betas[j];
            }

            hidden_layer_betas[i] = beta;

        }
        System.out.println("HL betas: " + Arrays.toString(hidden_layer_betas));

        // This is a HxO array (H hidden nodes, O outputs)
        double[][] delta_output_layer_weights = new double[num_hidden][num_outputs];
        //Calculate output layer weight changes.
        for(int i = 0; i < hidden_layer_outputs.length; i++){
            for(int j = 0; j < output_layer_outputs.length; j++){
                double slope = output_layer_outputs[j] * (1 - output_layer_outputs[j]);
                double weightdiff = learning_rate*hidden_layer_outputs[i]*slope*output_layer_betas[j];
                delta_output_layer_weights[i][j] = weightdiff;
            }
        }

        // This is a IxH array (I input, H hidden nodes)
        double[][] delta_hidden_layer_weights = new double[num_inputs][num_hidden];
        //Calculate hidden layer weight changes.
        for(int i = 0; i < inputs.length; i++){
            for(int j = 0; j < hidden_layer_outputs.length; j++){
                double slope = hidden_layer_outputs[j] * (1 - hidden_layer_outputs[j]);
                double weightdiff = learning_rate*inputs[i]*slope*hidden_layer_betas[j];
                delta_hidden_layer_weights[i][j] = weightdiff;
            }
        }

        // Return the weights we calculated, so they can be used to update all the weights.
        return new double[][][]{delta_output_layer_weights, delta_hidden_layer_weights};
    }

    public void update_weights(double[][] delta_output_layer_weights, double[][] delta_hidden_layer_weights) {
        //Update the weights
        for (int i = 0; i < this.output_layer_weights.length; i++) {
            for (int j = 0; j < this.output_layer_weights[i].length; j++) {
                output_layer_weights[i][j] += delta_output_layer_weights[i][j];
            }
        }

        for (int i = 0; i < this.hidden_layer_weights.length; i++) {
            for (int j = 0; j < this.hidden_layer_weights[i].length; j++) {
                hidden_layer_weights[i][j] += delta_hidden_layer_weights[i][j];
            }
        }
        System.out.println("Weights updated.");
    }

    public void train(double[][] instances, int[] desired_outputs, int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.println("epoch = " + epoch);
            int[] predictions = new int[instances.length];
            for (int i = 0; i < instances.length; i++) {
                double[] instance = instances[i];
                double[][] outputs = forward_pass(instance);
                double[][][] delta_weights = backward_propagate_error(instance, outputs[0], outputs[1], desired_outputs[i]);
                int predicted_class = predict(instances)[i];
                predictions[i] = predicted_class;

                //We use online learning, i.e. update the weights after every instance.
                update_weights(delta_weights[0], delta_weights[1]);
            }

            // Print new weights
            System.out.println("Hidden layer weights \n" + Arrays.deepToString(hidden_layer_weights));
            System.out.println("Output layer weights  \n" + Arrays.deepToString(output_layer_weights));

            //Print accuracy achieved over this epoch
            double acc = Double.NaN;
            double correct = 0.0;
            for(int i = 0; i < predictions.length; i++){
                int prediction = predictions[i];
                if(prediction == desired_outputs[i]) correct++;
            }
            acc = (correct / instances.length)*100;
            System.out.println(correct+" out of "+instances.length+ " correct results.");
            System.out.println("Accuracy for epoch "+epoch+" = " + acc + "%");
        }
    }

    public int[] predict(double[][] instances) {
        int[] predictions = new int[instances.length];
        for (int i = 0; i < instances.length; i++) {
            double[] instance = instances[i];
            double[][] outputs = forward_pass(instance);
            int predicted_class = -1;  //Should be 0, 1, or 2.
            double[] output_layer_outputs = outputs[1];
            double max = Double.NEGATIVE_INFINITY;
            for(int j = 0; j < output_layer_outputs.length; j++){
                double realOutput = output_layer_outputs[j];
                if(realOutput > max){
                    max = realOutput;
                    predicted_class = j;
                }
            }
            predictions[i] = predicted_class;
        }
        return predictions;
    }

}
