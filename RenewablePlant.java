package edu.uoc.uoctron.model;

public class RenewablePlant extends PowerPlant {

    private double efficiency;
    private String type;

    public RenewablePlant(String id, String name, String city, double maxCapacityMW, String availability, int restartMinutes,
                          double stability, String image, double latitude, double longitude, String type) {
        super(id, name, city, maxCapacityMW, availability, restartMinutes, stability, image, latitude, longitude);
        this.efficiency = 1.0;
        this.type = type;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public double getEfficiency() {
        return efficiency;
    }

    @Override
    public String getType() {
        return type;
    }
}
