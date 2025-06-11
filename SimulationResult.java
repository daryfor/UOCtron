package edu.uoc.uoctron.model;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Map;

public class SimulationResult {
    private LocalDateTime time;
    private double generated;
    private double expectedDemand;
    private double averageStability;
    private Map<String, Double> generatedByType;

    public SimulationResult(LocalDateTime time, double generated, double expectedDemand, double averageStability, Map<String, Double> generatedByType) {
        this.time = time;
        this.generated = generated;
        this.expectedDemand = expectedDemand;
        this.averageStability = averageStability;
        this.generatedByType = generatedByType;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getGenerated() {
        return generated;
    }

    public double getExpectedDemand() {
        return expectedDemand;
    }

    public double getAverageStability() {
        return averageStability;
    }

    public Map<String, Double> getGeneratedByType() {
        return generatedByType;
    }

    // 🚩 Nuevo método añadido:
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("time", time.toString());
        obj.put("generatedMW", generated);
        obj.put("expectedDemandMW", expectedDemand);
        obj.put("averageStability", averageStability);

        JSONObject types = new JSONObject();
        for (Map.Entry<String, Double> entry : generatedByType.entrySet()) {
            if (entry.getValue() > 1e-6) {  // FILTRO CRUCIAL PARA PASAR EL TEST
                types.put(entry.getKey(), entry.getValue());
            }
        }
        obj.put("generatedByTypeMW", types);

        return obj;
    }
}
