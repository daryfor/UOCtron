package edu.uoc.uoctron.model;

import java.time.LocalDateTime;
import java.util.List;

public class Simulation {
    private LocalDateTime startTime;
    private List<SimulationResult> results;

    public Simulation(LocalDateTime startTime, List<SimulationResult> results) {
        this.startTime = startTime;
        this.results = results;
    }

    public List<SimulationResult> getResults() {
        return results;
    }
}
