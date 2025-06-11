package edu.uoc.uoctron.model;

import java.time.LocalDateTime;

public class DemandData {
    private LocalDateTime time;
    private double demand;

    public DemandData(LocalDateTime time, double demand) {
        this.time = time;
        this.demand = demand;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getDemand() {
        return demand;
    }
}
