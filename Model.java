package edu.uoc.uoctron.model;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<PowerPlant> plants;
    private List<DemandData> demand;
    private List<Simulation> simulations;

    public Model() {
        plants = new ArrayList<>();
        demand = new ArrayList<>();
        simulations = new ArrayList<>();
    }

    public List<PowerPlant> getPlants() {
        return plants;
    }

    public List<DemandData> getDemand() {
        return demand;
    }

    public List<Simulation> getSimulations() {
        return simulations;
    }

    // 🔧 Nuevo método de control total de simulaciones:
    public void setSimulation(Simulation simulation) {
        simulations.clear();
        simulations.add(simulation);
    }
}
