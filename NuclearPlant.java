package edu.uoc.uoctron.model;

public class NuclearPlant extends PowerPlant {

    public NuclearPlant(String id, String name, String city, double maxCapacityMW, String availability, int restartMinutes,
                        double stability, String image, double latitude, double longitude) {
        super(id, name, city, maxCapacityMW, availability, restartMinutes, stability, image, latitude, longitude);
    }

    @Override
    public String getType() {
        return "Nuclear";
    }
}
