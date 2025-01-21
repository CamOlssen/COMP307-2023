public class Tuple {
    private double dist;
    private double label;

    Tuple(Double dist, Double label){
        this.dist = dist;
        this.label = label;
    }

    double getDist(){
        return dist;
    }
    double getLabel(){
        return label;
    }

}
