import java.util.*;
public class Instance {
    private List<String> values = new ArrayList<String>();
    private String instanceLabel;

    public Instance(String[] data){
        this.instanceLabel = data[1];
        for(int i=2; i < data.length; i++) values.add(data[i]);
    }

    public String getAttribute(int index){
        return values.get(index);
    }

    public List<String> getValues() {
        return values;
    }

    public String getInstanceLabel() {
        return instanceLabel;
    }

    public String toString(){
        return values.toString();
    }
}
