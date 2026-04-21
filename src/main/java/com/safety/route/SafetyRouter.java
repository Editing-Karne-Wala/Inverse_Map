package com.safety.route;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.time.LocalTime;
import java.util.List;

public class SafetyRouter {
    private final Graph<RoadNode, RoadSegment> graph;

    private CrowdService crowdService;
    private final List<RiskZone> riskZones;

    public SafetyRouter(CrowdService crowdService) {
        this.graph = new SimpleWeightedGraph<>(RoadSegment.class);
        this.crowdService = crowdService;
        this.riskZones = new java.util.ArrayList<>();
    }

    public void addRiskZone(RiskZone zone) {
        riskZones.add(zone);
    }

    public void addNode(RoadNode node) {
        graph.addVertex(node);
    }

    public RoadSegment addRoad(RoadNode source, RoadNode target, double distance, String name) {
        RoadSegment segment = graph.addEdge(source, target);
        if (segment != null) {
            segment.setPhysicalDistance(distance);
            segment.setName(name);
            
            // Initial score
            double density = crowdService.getCrowdDensity(segment, LocalTime.now());
            segment.setCrowdFactor(density);
            
            // Calculate risk based on mid-point proximity to risk zones
            double midLat = (source.latitude() + target.latitude()) / 2.0;
            double midLon = (source.longitude() + target.longitude()) / 2.0;
            segment.setRiskScore(calculateRisk(midLat, midLon));
            
            updateEffectiveWeight(segment);
        }
        return segment;
    }

    private double calculateRisk(double lat, double lon) {
        double maxRisk = 1.0;
        for (RiskZone zone : riskZones) {
            if (zone.isInside(lat, lon)) {
                maxRisk = Math.max(maxRisk, zone.riskMultiplier());
            }
        }
        return maxRisk;
    }

    /**
     * Updates the graph internal weight based on current crowd factor.
     */
    private void updateEffectiveWeight(RoadSegment segment) {
        graph.setEdgeWeight(segment, segment.getSafetyWeight());
    }

    /**
     * Updates crowd factors based on the provided service and time.
     */
    public void refreshCrowdFactors(LocalTime time) {
        for (RoadSegment segment : graph.edgeSet()) {
            double density = crowdService.getCrowdDensity(segment, time);
            segment.setCrowdFactor(density);
            // Risk is static for now, but could be temporal too
            updateEffectiveWeight(segment);
        }
    }

    public List<RoadNode> findSafestRoute(RoadNode start, RoadNode end) {
        DijkstraShortestPath<RoadNode, RoadSegment> dijkstra = new DijkstraShortestPath<>(graph);
        var path = dijkstra.getPath(start, end);
        return path != null ? path.getVertexList() : null;
    }

    public List<RoadSegment> getRouteSegments(RoadNode start, RoadNode end) {
        DijkstraShortestPath<RoadNode, RoadSegment> dijkstra = new DijkstraShortestPath<>(graph);
        var path = dijkstra.getPath(start, end);
        return path != null ? path.getEdgeList() : null;
    }
}
