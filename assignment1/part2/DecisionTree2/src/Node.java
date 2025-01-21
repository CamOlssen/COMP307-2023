public class Node {
    private String best = "";
    private Node left;
    private Node right;
    private boolean leaf = false;
    private String nodeClass;
    double prob;

    //non-leaf node
    public Node(String best, Node left, Node right){
        this.best = best;
        this.left = left;
        this.right = right;
    }

    public Node(String nodeClass, double prob){
        leaf = true;
        this.nodeClass = nodeClass;
        this.prob = prob;
    }

    public boolean isLeaf(){
        return leaf;
    }
    public String getBest(){
        return best;
    }

    public Node getLeft(){
        return left;
    }

    public Node getRight() {
        return right;
    }

    public double getProb() {
        return prob;
    }

    public String getNodeClass() {
        return nodeClass;
    }
}
