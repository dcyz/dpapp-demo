package models.structs;

import java.util.ArrayList;
import java.util.HashMap;

public class Query {
    private HashMap<String, Double> args;
    private int scale;
    private ArrayList<ArrayList<Double>> areas;

    public int getScale() {
        return scale;
    }

    public HashMap<String, Double> getArgs() {
        return args;
    }

    public ArrayList<ArrayList<Double>> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<ArrayList<Double>> areas) {
        this.areas = areas;
    }

    public void setArgs(HashMap<String, Double> args) {
        this.args = args;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
