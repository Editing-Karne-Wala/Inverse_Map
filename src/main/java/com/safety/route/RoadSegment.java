package com.safety.route;

import org.jgrapht.graph.DefaultWeightedEdge;

public class RoadSegment extends DefaultWeightedEdge {
    private double physicalDistance;
    private double crowdFactor = 0.5; 
    private double riskScore = 1.0; // 1.0 is neutral, higher is more dangerous
    private String name;

    public RoadSegment() {
        super();
    }

    public void setPhysicalDistance(double physicalDistance) {
        this.physicalDistance = physicalDistance;
    }

    public double getPhysicalDistance() {
        return physicalDistance;
    }

    public void setCrowdFactor(double crowdFactor) {
        this.crowdFactor = Math.max(0.1, Math.min(1.0, crowdFactor));
    }

    public double getCrowdFactor() {
        return crowdFactor;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * The effective weight used for Dijkstra. 
     * We balance Distance vs (Risk / Crowd).
     * Alpha controls how much we value safety over time.
     */
    public double getSafetyWeight() {
        double alpha = 2.0; // Safety sensitivity
        double safetyFactor = riskScore / Math.pow(crowdFactor, alpha);
        return physicalDistance * safetyFactor;
    }

    @Override
    public String toString() {
        return String.format("%s (Dist: %.1f, Crowd: %.2f)", name != null ? name : "Segment", physicalDistance, crowdFactor);
    }
}
