import java.util.*;
public class Instance {
    private int category = -1;
    private ArrayList<Double> values = new ArrayList<>();

    public Instance(){
        values.add(1.0);
    }

    public void setCategory(String catString){
        if (catString.equals("g")) category = 1;
        else if (catString.equals("b")) category = 0;
    }

    public void addVal(double val){
        values.add(val);
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public Double getAttributeIndex(int index){
        return values.get(index);
    }

    public int getCategory() {
        return category;
    }


}
