public class Merge implements Comparable<Merge>{
    private int ID1;
    private int ID2;
    private double saving;
    public Merge(int ID1, int ID2, double saving){
        this.ID1 = ID1;
        this.ID2 = ID2;
        this.saving = saving;
    }

    @Override
    public String toString() {
        return ID1 + " - " + ID2 + ": saving " +saving;
    }

    @Override
    public int compareTo(Merge other) {
        if(this.saving > other.saving){
            return -1;
        }
        else if(this.saving < other.saving){
            return 1;
        }
        else{
            return 0;
        }
    }

    public int getID1() {
        return ID1;
    }

    public int getID2() {
        return ID2;
    }

    public double getSaving() {
        return saving;
    }
}
