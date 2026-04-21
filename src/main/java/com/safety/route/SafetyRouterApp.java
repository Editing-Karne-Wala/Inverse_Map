package com.safety.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class SafetyRouterApp {

    private final SafetyRouter router;
    private final CrowdService crowdService;

    public SafetyRouterApp(LiveGoogleTrafficService liveTrafficService) {
        this.crowdService = liveTrafficService;
        this.router = new SafetyRouter(crowdService);
        initializeKolkataGraph();
    }

    private void initializeKolkataGraph() {
        // Madhyamgram to Rajarhat Setup
        RoadNode madhyamgram = new RoadNode("Madhyamgram-Chowmatha", 22.6894, 88.4459);
        RoadNode internalKolkata = new RoadNode("Internal-Rajarhat-Lanes", 22.6500, 88.4500);
        RoadNode newTownMain = new RoadNode("New-Town-Expressway", 22.6200, 88.4400);
        RoadNode rajarhatCentral = new RoadNode("Rajarhat-New-Town", 22.6236, 88.4410);

        router.addNode(madhyamgram);
        router.addNode(internalKolkata);
        router.addNode(newTownMain);
        router.addNode(rajarhatCentral);

        router.addRoad(madhyamgram, internalKolkata, 4.0, "Internal Village Path");
        router.addRoad(internalKolkata, rajarhatCentral, 5.0, "Gopalpur Internal Roads");
        router.addRoad(madhyamgram, newTownMain, 8.0, "NH-12 Jessore Road Expressway");
        router.addRoad(newTownMain, rajarhatCentral, 2.0, "Biswa Bangla Arterial Road");

        // Add Sonagachi Risk Zone
        router.addRiskZone(new RiskZone("Sonagachi Area", 22.5875, 88.3597, 500, 5.0));
    }

    @GetMapping("/api/route")
    public List<double[]> getSafeRoute() {
        router.refreshCrowdFactors(LocalTime.now());
        
        // Demo route: Madhyamgram to Rajarhat
        RoadNode start = new RoadNode("Madhyamgram-Chowmatha", 22.6894, 88.4459);
        RoadNode end = new RoadNode("Rajarhat-New-Town", 22.6236, 88.4410);
        
        List<RoadNode> nodes = router.findSafestRoute(start, end);
        List<double[]> coordinates = new ArrayList<>();
        if (nodes != null) {
            for (RoadNode node : nodes) {
                coordinates.add(new double[]{node.latitude(), node.longitude()});
            }
        }
        return coordinates;
    }

    public static void main(String[] args) {
        SpringApplication.run(SafetyRouterApp.class, args);
    }
}
