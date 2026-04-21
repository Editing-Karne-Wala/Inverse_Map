package com.safety.route;

import java.time.LocalTime;
import java.util.Random;

public class GoogleCrowdService implements CrowdService {
    private final Random random = new Random();

    @Override
    public double getCrowdDensity(RoadSegment segment, LocalTime time) {
        // In a real implementation, this would:
        // 1. Call Google Maps Traffic API for the segment's coordinates.
        // 2. Fetch 'Popular Times' for nearby businesses via Places API.
        
        double baseDensity = getBaseDensityForSegment(segment);
        double temporalFactor = getTemporalFactor(time);
        
        // Add a bit of 'live' randomness for demo purposes
        double liveFluctuation = 0.9 + (0.2 * random.nextDouble()); 
        
        return Math.min(1.0, baseDensity * temporalFactor * liveFluctuation);
    }

    private double getBaseDensityForSegment(RoadSegment segment) {
        // Logic: Main roads have higher baseline than residential ones.
        String name = segment.getName().toLowerCase();
        if (name.contains("main") || name.contains("high") || name.contains("commercial")) {
            return 0.8;
        }
        if (name.contains("residential") || name.contains("alley")) {
            return 0.3;
        }
        return 0.5;
    }

    private double getTemporalFactor(LocalTime time) {
        int hour = time.getHour();
        // Peak hours (Morning/Evening)
        if ((hour >= 8 && hour <= 10) || (hour >= 17 && hour <= 20)) {
            return 1.2;
        }
        // Dead of night
        if (hour >= 23 || hour <= 4) {
            return 0.4;
        }
        return 1.0;
    }
}
