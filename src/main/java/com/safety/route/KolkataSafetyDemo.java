package com.safety.route;

import java.time.LocalTime;
import java.util.List;

public class KolkataSafetyDemo {
    public static void main(String[] args) {
        CrowdService crowdService = new GoogleCrowdService();
        SafetyRouter router = new SafetyRouter(crowdService);

        // Define Risk Zones (e.g., Sonagachi)
        // 22.5875 N, 88.3597 E
        router.addRiskZone(new RiskZone("Sonagachi Area", 22.5875, 88.3597, 500, 5.0));

        // Define Nodes for Kolkata MVP
        RoadNode sectorV = new RoadNode("Sector-V", 22.5726, 88.4297); // Office
        RoadNode bypass = new RoadNode("EM-Bypass", 22.5600, 88.4000);
        RoadNode parkCircus = new RoadNode("Park-Circus-Connector", 22.5400, 88.3700);
        RoadNode howrah = new RoadNode("Howrah-Station", 22.5829, 88.3428); // Home
        
        // North Kolkata Route (Shortest but goes near Sonagachi)
        RoadNode northKolkata = new RoadNode("Shyam Bazar / North-Kolkata", 22.5950, 88.3650);
        RoadNode sonagachiEdge = new RoadNode("Jatindra Mohan Ave", 22.5870, 88.3590);

        router.addNode(sectorV);
        router.addNode(bypass);
        router.addNode(parkCircus);
        router.addNode(howrah);
        router.addNode(northKolkata);
        router.addNode(sonagachiEdge);

        // --- THE SHORT OPTION (Through Sonagachi / North Kolkata Alleys) ---
        // Distance roughly 10km
        router.addRoad(sectorV, northKolkata, 6.0, "VIP Road / Ultadanga");
        router.addRoad(northKolkata, sonagachiEdge, 1.5, "North Kolkata Lanes (Adjacent to Red Light District)");
        router.addRoad(sonagachiEdge, howrah, 3.0, "Howrah Bridge Approach");

        // --- THE SAFE OPTION (Through Main Hubs & Flyovers) ---
        // Distance roughly 14km (Longer but very busy/popular)
        router.addRoad(sectorV, bypass, 4.0, "Sector V Main Road");
        router.addRoad(bypass, parkCircus, 5.0, "Maa Flyover (High Transit)");
        router.addRoad(parkCircus, howrah, 5.0, "AJC Bose Road / Central Transit");

        System.out.println("\n=== NEW TEST: MADHYAMGRAM TO RAJARHAT NEW TOWN ===");
        System.out.println("Time: 11:30 PM (Late Night commute)");
        
        RoadNode madhyamgram = new RoadNode("Madhyamgram-Chowmatha", 22.6894, 88.4459);
        RoadNode internalKolkata = new RoadNode("Internal-Rajarhat-Lanes", 22.6500, 88.4500);
        RoadNode newTownMain = new RoadNode("New-Town-Expressway", 22.6200, 88.4400);
        RoadNode rajrarhatCentral = new RoadNode("Rajarhat-New-Town", 22.6236, 88.4410);

        router.addNode(madhyamgram);
        router.addNode(internalKolkata);
        router.addNode(newTownMain);
        router.addNode(rajrarhatCentral);

        // Path A: Shortest / Internal (Narrow, Dark)
        router.addRoad(madhyamgram, internalKolkata, 4.0, "Badu Road / Internal Village Path");
        router.addRoad(internalKolkata, rajrarhatCentral, 5.0, "Rajarhat-Gopalpur Internal Roads");

        // Path B: Safe / Highway (Lighted, Busy, CCTV)
        router.addRoad(madhyamgram, newTownMain, 8.0, "NH-12 (Jessore Road) - Busy Hub");
        router.addRoad(newTownMain, rajrarhatCentral, 2.0, "Major Arterial Road (Biswa Bangla)");

        router.refreshCrowdFactors(LocalTime.of(23, 30));
        printSafePath(router, madhyamgram, rajrarhatCentral);
    }

    private static void printSafePath(SafetyRouter router, RoadNode start, RoadNode end) {
        List<RoadSegment> path = router.getRouteSegments(start, end);
        if (path == null) {
            System.out.println("No safe path found.");
            return;
        }

        double totalDist = 0;
        System.out.println("Recommended Route:");
        for (RoadSegment s : path) {
            System.out.printf(" >> %s [Dist: %.1f km, Crowd: %.2f, Risk: %.1f]\n", 
                s.getName(), s.getPhysicalDistance(), s.getCrowdFactor(), s.getRiskScore());
            totalDist += s.getPhysicalDistance();
        }
        System.out.println("-------------------------------------------------");
        System.out.printf("Total Real Distance: %.2f km\n", totalDist);
        System.out.println("Rationale: Preferring main flyovers and commercial hubs over narrow lanes near high-risk zones.");
    }
}
