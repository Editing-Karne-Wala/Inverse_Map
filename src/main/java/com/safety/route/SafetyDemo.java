package com.safety.route;

import java.time.LocalTime;
import java.util.List;

public class SafetyDemo {
    public static void main(String[] args) {
        CrowdService crowdService = new GoogleCrowdService();
        SafetyRouter router = new SafetyRouter(crowdService);

        // Coordinates for demo
        RoadNode start = new RoadNode("A", 12.9716, 77.5946); // Home
        RoadNode desertedNode = new RoadNode("B", 12.9720, 77.5950); // Dark Alley
        RoadNode crowdedNode = new RoadNode("C", 12.9710, 77.6000); // Main Market
        RoadNode end = new RoadNode("D", 12.9730, 77.6050); // Station

        router.addNode(start);
        router.addNode(desertedNode);
        router.addNode(crowdedNode);
        router.addNode(end);

        // Path 1 (Short but Deserted/Empty)
        // Total Dist: 5.0 units
        router.addRoad(start, desertedNode, 2.0, "Residential Alleyway");
        router.addRoad(desertedNode, end, 3.0, "Deserted Industrial Path");

        // Path 2 (Longer but Crowded/Market)
        // Total Dist: 8.0 units
        router.addRoad(start, crowdedNode, 4.0, "Busy Main Street");
        router.addRoad(crowdedNode, end, 4.0, "Bright Commercial Hub");

        System.out.println("--- Scenario: Finding the safest route at NIGHT (3 AM) ---");
        router.refreshCrowdFactors(LocalTime.of(3, 0));
        printRoute(router, start, end);

        System.out.println("\n--- Scenario: Finding the safest route during DAY (2 PM) ---");
        router.refreshCrowdFactors(LocalTime.of(14, 0));
        printRoute(router, start, end);
    }

    private static void printRoute(SafetyRouter router, RoadNode start, RoadNode end) {
        List<RoadSegment> segments = router.getRouteSegments(start, end);
        if (segments == null) {
            System.out.println("No path found.");
            return;
        }

        double totalDist = 0;
        System.out.println("Recommended Path:");
        for (RoadSegment s : segments) {
            System.out.printf(" - %s [Dist: %.1f, Crowd: %.2f]\n", s.getName(), s.getPhysicalDistance(), s.getCrowdFactor());
            totalDist += s.getPhysicalDistance();
        }
        System.out.printf("Total Real Distance: %.1f\n", totalDist);
    }
}
