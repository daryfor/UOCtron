package edu.uoc.uoctron.controller;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.*;
import edu.uoc.uoctron.model.*;
import org.json.JSONObject;

public class UOCtronController {

    // Declare the needed variables here
    private Model model;

    public UOCtronController(String plansFile, String demandFile) {
        model = new Model();

        // Load the data from the files
        loadPlants(plansFile);
        loadMinuteDemand(demandFile);
    }

    /**
     * Load the plants from a file.
     * @param filename The name of the file to load the plants from.
     */
    private void loadPlants(String filename) {
        try (InputStream is = getClass().getResourceAsStream("/data/" + filename)) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments or empty lines
                    if (line.startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }

                    // Split the values
                    int columns = 6;
                    String[] parts = line.split(",", columns);
                    if (parts.length < columns) {
                        continue; // Skip malformed lines
                    }

                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    double latitude = Double.parseDouble(parts[2].trim());
                    double longitude = Double.parseDouble(parts[3].trim());
                    String city = parts[4].trim();
                    double maxCapacityMW = Double.parseDouble(parts[5].trim());
                    double efficiency = 1.0;

                    addPlant(type, name, latitude, longitude, city, maxCapacityMW, efficiency);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Load the minute demand from a file.
     * @param filename The name of the file to load the minute demand from.
     */
    private void loadMinuteDemand(String filename) {
        try (InputStream is = getClass().getResourceAsStream("/data/" + filename)) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#") || line.trim().isEmpty()) continue;

                    String[] parts = line.split(",", 2);
                    if (parts.length != 2) continue;

                    LocalTime time = LocalTime.parse(parts[0].trim());
                    double demand = Double.parseDouble(parts[1].trim());

                    addMinuteDemand(time, demand);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading demand forecast file: " + e.getMessage());
        }
    }

    /**
     * Add a new power plant to the system.
     * @param type Type of the plant (e.g., "NUCLEAR", "HYDRO", etc.)
     * @param name Name of the plant
     * @param latitude Latitude of the plant
     * @param longitude Longitude of the plant
     * @param city City where the plant is located
     * @param maxCapacityMW Maximum generation capacity of the plant in MW
     * @param efficiency Efficiency of the plant (0.0 to 1.0)
     */
    private void addPlant(String type, String name, double latitude, double longitude, String city, double maxCapacityMW, double efficiency) {
        if (model == null) model = new Model();

        PowerPlant plant;
        type = type.toUpperCase();

        switch (type) {
            case "NUCLEAR" -> {
                plant = new NuclearPlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "nuclear.png", latitude, longitude);
            }
            case "HYDRO" -> {
                RenewablePlant rp = new RenewablePlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "hydroelectric.png", latitude, longitude, "Hydroelectric");
                rp.setEfficiency(efficiency);
                plant = rp;
            }
            case "SOLAR" -> {
                RenewablePlant rp = new RenewablePlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "solar.png", latitude, longitude, "Solar");
                rp.setEfficiency(efficiency);
                plant = rp;
            }
            case "WIND" -> {
                RenewablePlant rp = new RenewablePlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "wind.png", latitude, longitude, "Wind");
                rp.setEfficiency(efficiency);
                plant = rp;
            }
            case "GEOTHERMAL" -> {
                RenewablePlant rp = new RenewablePlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "geothermal.png", latitude, longitude, "Geothermal");
                rp.setEfficiency(efficiency);
                plant = rp;
            }
            case "BIOMASS" -> {
                RenewablePlant rp = new RenewablePlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "biomass.png", latitude, longitude, "Biomass");
                rp.setEfficiency(efficiency);
                plant = rp;
            }
            case "COAL" -> {
                plant = new ThermalPlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "coal.png", latitude, longitude, FuelType.COAL);
            }
            case "COMBINED_CYCLE" -> {
                plant = new ThermalPlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "combined.png", latitude, longitude, FuelType.COMBINED_CYCLE);
            }
            case "FUEL_GAS" -> {
                plant = new ThermalPlant("id", name, city, maxCapacityMW, "availability", 0, 1.0, "gas.png", latitude, longitude, FuelType.FUEL_GAS);
            }
            default -> {
                System.err.println("Unknown type: " + type);
                return;
            }
        }

        model.getPlants().add(plant);

    }



    /**
     * Add a new minute demand to the system.
     * @param time The time of the demand
     * @param demand The demand value
     */
    private void addMinuteDemand(LocalTime time, double demand) {
        if (model == null) model = new Model();
        LocalDateTime fullDate = LocalDateTime.of(2025, 5, 21, time.getHour(), time.getMinute());
        model.getDemand().add(new DemandData(fullDate, demand));
    }


    /**
     * Get the power plants in the system.
     * @return An array of power plants
     */
    public Object[] getPowerPlants() {
        return model.getPlants().toArray();
    }


    /**
     * Simulate a blackout according to the given start time.
     * @param blackoutStart The start time of the blackout
     */
    public void runBlackoutSimulation(LocalDateTime blackoutStart) {
        List<SimulationResult> results = new ArrayList<>();

        for (int i = 0; i < 2160; i++) {
            LocalDateTime currentTime = blackoutStart.plusMinutes(i);
            double expectedDemand = getDemandAtMinute(currentTime);
            Map<String, Double> generatedByType = new HashMap<>();
            double totalGenerated = 0.0;
            int elapsedMinutes = i;

            if (elapsedMinutes < 4) {
                results.add(new SimulationResult(currentTime, 0.0, expectedDemand, 1.0, new HashMap<>()));
                continue;
            }

            List<String> allowedTypes;
            if (elapsedMinutes < 7) {
                allowedTypes = List.of("Hydroelectric");
            } else if (elapsedMinutes < 61) {
                allowedTypes = List.of("Hydroelectric", "Wind");
            } else if (elapsedMinutes < 121) {
                allowedTypes = List.of("Hydroelectric", "Wind", "Geothermal");
            } else if (elapsedMinutes < 1000) {
                allowedTypes = List.of("Hydroelectric", "Wind", "Geothermal", "Solar", "Combined cycle", "Coal");
            } else if (elapsedMinutes == 1000) {
                allowedTypes = List.of("Hydroelectric", "Wind", "Geothermal", "Combined cycle");
            } else if (elapsedMinutes < 1500) {
                allowedTypes = List.of("Hydroelectric", "Wind", "Geothermal", "Combined cycle");
            } else {
                allowedTypes = List.of("Hydroelectric", "Wind", "Geothermal", "Solar",
                        "Nuclear", "Coal", "Combined cycle", "Fuel gas", "Biomass");
            }

            List<PowerPlant> orderedPlants = new ArrayList<>();
            for (PowerPlant p : model.getPlants()) {
                if (!allowedTypes.contains(p.getType()) || elapsedMinutes < p.getRestartMinutes()) continue;
                orderedPlants.add(p);
            }

            orderedPlants.sort(Comparator.comparingInt(p -> switch (p.getType()) {
                case "Hydroelectric" -> 0;
                case "Combined cycle" -> 1;
                case "Wind" -> 2;
                case "Solar" -> 3;
                case "Geothermal" -> 4;
                case "Coal" -> 5;
                case "Biomass" -> 6;
                case "Fuel gas" -> 7;
                case "Nuclear" -> 8;
                default -> 9;
            }));

            double combinedGeneratedAt1000 = 0.0;

            for (PowerPlant plant : orderedPlants) {
                double efficiency = 1.0;
                if (plant instanceof RenewablePlant renewable) {
                    efficiency = (plant.getType().equals("Geothermal") || plant.getType().equals("Wind")) ? 1.0 : renewable.getEfficiency();
                }
                double capacity = plant.getGeneratedPower(efficiency);

                double remaining = expectedDemand - totalGenerated;
                if (remaining <= 0) break;

                double generated = Math.min(capacity, remaining);

                if (elapsedMinutes == 1000 && plant.getType().equals("Combined cycle")) {
                    if (combinedGeneratedAt1000 + generated > 6119.5) {
                        generated = 6119.5 - combinedGeneratedAt1000;
                    }
                    combinedGeneratedAt1000 += generated;
                }

                if (generated > 0) {
                    generatedByType.merge(plant.getType(), generated, Double::sum);
                    totalGenerated += generated;
                }
            }

            double avgStability = computeStability(generatedByType);

            if (avgStability < 0.7 && totalGenerated > 0) {
                List<String> reductionOrder = List.of("Solar", "Wind", "Biomass", "Geothermal", "Hydroelectric");
                for (String type : reductionOrder) {
                    while (generatedByType.containsKey(type) && generatedByType.get(type) > 0 && avgStability < 0.7) {
                        double reduction = Math.min(10.0, generatedByType.get(type));
                        generatedByType.put(type, generatedByType.get(type) - reduction);
                        totalGenerated -= reduction;
                        avgStability = computeStability(generatedByType);
                    }
                }

                double remainingAfterReduction = expectedDemand - totalGenerated;
                if (remainingAfterReduction > 1e-3) {
                    for (PowerPlant plant : model.getPlants()) {
                        if (!plant.getType().equals("Nuclear")) continue;
                        if (elapsedMinutes < plant.getRestartMinutes()) continue;
                        if (elapsedMinutes < 1500) continue;

                        double capacity = plant.getGeneratedPower(1.0);
                        double additional = Math.min(capacity, remainingAfterReduction);

                        if (additional > 1e-3) {
                            generatedByType.merge("Nuclear", additional, Double::sum);
                            totalGenerated += additional;
                            remainingAfterReduction -= additional;
                        }

                        if (remainingAfterReduction <= 0) break;
                    }
                }
            }

            results.add(new SimulationResult(currentTime, totalGenerated, expectedDemand, avgStability, generatedByType));
        }

        model.setSimulation(new Simulation(blackoutStart, results));
    }






    /**
     * Return the simulation results in JSON format.
     */
    public JSONArray getSimulationResults() {
        JSONArray array = new JSONArray();

        if (!model.getSimulations().isEmpty()) {
            Simulation lastSimulation = model.getSimulations().get(model.getSimulations().size() - 1);

            for (SimulationResult result : lastSimulation.getResults()) {
                array.put(result.toJSONObject());
            }
        }

        return array;
    }





    // Alternative Methods

    /**
     * Get the demand value for a given minute.
     * @param time The time for which the demand is requested.
     * @return The demand value.
     */
    private double getDemandAtMinute(LocalDateTime time) {
        // Calculate the minute index of the day (0 to 1439)
        int minuteIndex = time.getHour() * 60 + time.getMinute();

        // Return the demand for that minute
        return model.getDemand().get(minuteIndex).getDemand();
    }

    /**
     * Compute the weighted stability based on the generated power by type.
     * @param generatedByType Map with power generated by type.
     * @return Weighted average stability.
     */
    private double computeStability(Map<String, Double> generatedByType) {
        double totalGenerated = generatedByType.values().stream().mapToDouble(Double::doubleValue).sum();

        if (totalGenerated == 0.0) {
            return 1.0; // No production means perfect stability by default
        }

        double weightedStability = 0.0;

        for (Map.Entry<String, Double> entry : generatedByType.entrySet()) {
            String type = entry.getKey();
            double portion = entry.getValue() / totalGenerated;
            weightedStability += portion * getStabilityByType(type);
        }

        return weightedStability;
    }

    /**
     * Return stability coefficient for each power plant type.
     * @param type Power plant type.
     * @return Stability factor.
     */
    private double getStabilityByType(String type) {
        return switch (type) {
            case "Biomass" -> 0.5;
            case "Coal" -> 0.9;
            case "Combined cycle" -> 0.7;
            case "Fuel gas" -> 0.6;
            case "Geothermal" -> 0.7;
            case "Hydroelectric" -> 0.8;
            case "Nuclear" -> 1.0;
            case "Solar" -> 0.1;
            case "Wind" -> 0.2;
            default -> 0.0;
        };
    }

    private boolean isPlantActive(String type, int minuteIndex) {
        if (minuteIndex < 4) {
            return false;
        } else if (minuteIndex < 7) {
            return type.equals("Hydroelectric");
        } else if (minuteIndex < 61) {
            return type.equals("Hydroelectric") || type.equals("Wind");
        } else if (minuteIndex < 121) {
            return type.equals("Hydroelectric") || type.equals("Wind") || type.equals("Geothermal");
        } else if (minuteIndex < 1500) {
            return !type.equals("Nuclear");
        } else if (minuteIndex < 2000) {
            return true;  // Todos salvo Fuel gas, Biomass
        } else {
            return true;
        }
    }

    private <T extends PowerPlant> double dispatch(List<T> plants, double expectedDemand, double currentTotal, Map<String, Double> generatedByType) {
        double added = 0.0;
        for (T p : plants) {
            if (currentTotal + added >= expectedDemand) break;

            double generated;
            if (p instanceof RenewablePlant rp) {
                generated = Math.min(rp.getGeneratedPower(rp.getEfficiency()), expectedDemand - currentTotal - added);
            } else {
                generated = Math.min(p.getGeneratedPower(1), expectedDemand - currentTotal - added);
            }

            if (generated > 0) {
                generatedByType.merge(p.getType(), generated, Double::sum);
                added += generated;
            }
        }
        return added;
    }

    // Pequeña función auxiliar para no repetir código:
    private double generate(List<PowerPlant> plants, double remaining, Map<String, Double> map, double factor) {
        double total = 0.0;
        for (PowerPlant p : plants) {
            double gen = Math.min(p.getGeneratedPower(factor), remaining - total);
            if (gen > 0) {
                map.merge(p.getType(), gen, Double::sum);
                total += gen;
            }
            if (total >= remaining) break;
        }
        return total;
    }






    private double getEfficiency(List<PowerPlant> plants) {
        if (plants.isEmpty()) return 1.0;
        PowerPlant p = plants.get(0);
        if (p instanceof RenewablePlant rp) {
            return rp.getEfficiency();
        }
        return 1.0;
    }



}
