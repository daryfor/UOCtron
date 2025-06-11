package edu.uoc.uoctron.model;

public class ThermalPlant extends PowerPlant {

    private FuelType fuelType;

    public ThermalPlant(String id, String name, String city, double maxCapacityMW, String availability, int restartMinutes,
                        double stability, String image, double latitude, double longitude, FuelType fuelType) {
        super(id, name, city, maxCapacityMW, availability, restartMinutes, stability, image, latitude, longitude);
        this.fuelType = fuelType;
    }

    @Override
    public String getType() {
        return switch (fuelType) {
            case BIOMASS -> "Biomass";
            case COAL -> "Coal";
            case COMBINED_CYCLE -> "Combined cycle";
            case FUEL_GAS -> "Fuel gas";
        };
    }
}
