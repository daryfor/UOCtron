package edu.uoc.uoctron.model;

import org.json.JSONObject;

public abstract class PowerPlant {
    protected String id;
    protected String name;
    protected String city;
    protected double maxCapacityMW;
    protected String availability;
    protected int restartMinutes;
    protected double stability;
    protected String image;
    protected double latitude;
    protected double longitude;

    public PowerPlant(String id, String name, String city, double maxCapacityMW, String availability, int restartMinutes,
                      double stability, String image, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.maxCapacityMW = maxCapacityMW;
        this.availability = availability;
        this.restartMinutes = restartMinutes;
        this.stability = stability;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getRestartMinutes() {
        return restartMinutes;
    }

    public double getGeneratedPower(double factor) {
        return maxCapacityMW * factor;
    }

    public double getStability() {
        return stability;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name != null ? name : "");
        obj.put("type", this.getType() != null ? this.getType() : "");
        obj.put("icon", image != null ? image : "");
        obj.put("city", city != null ? city : "");
        obj.put("latitude", latitude);
        obj.put("longitude", longitude);
        obj.put("maxCapacityMW", maxCapacityMW);
        return obj;
    }

    @Override
    public String toString() {
        try {
            return this.toJSON().toString();
        } catch (Exception e) {
            return "{}"; // String JSON vacío
        }
    }


    public abstract String getType();
}
