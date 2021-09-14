package models.structs;

public class AreaStat {
    private double StartLng;
    private double StartLat;
    private double EndLng;
    private double EndLat;
    private int Count;

    public int getCount() {
        return Count;
    }

    public double getEndLat() {
        return EndLat;
    }

    public double getEndLng() {
        return EndLng;
    }

    public double getStartLat() {
        return StartLat;
    }

    public double getStartLng() {
        return StartLng;
    }
}
