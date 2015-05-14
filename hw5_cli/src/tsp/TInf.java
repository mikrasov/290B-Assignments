package tsp;


import java.util.ArrayList;

public class TInf {

    private double runtime;
    private ArrayList<Long> parentUids;

    public TInf(double runtime, ArrayList<Long> parentUids){

        this.runtime = runtime;
        this.parentUids = parentUids;
    }

    public double getRuntime() {
        return runtime;
    }

    public ArrayList<Long> getParentUids() {
        return parentUids;
    }


}
