package com.safety.route;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalTime;

@Service
public class LiveGoogleTrafficService implements CrowdService {
    
    @Value("${google.directions.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public double getCrowdDensity(RoadSegment segment, LocalTime time) {
        // We use the 'duration_in_traffic' vs 'duration' to estimate crowd/active density
        try {
            // Note: This logic assumes we have source/target nodes linked to the segment
            // For MVP, we use the segment name to detect if it's Arterial (Arterial roads have higher base density)
            double baseDensity = 0.4;
            if (segment.getName().toLowerCase().contains("highway") || 
                segment.getName().toLowerCase().contains("expressway") ||
                segment.getName().toLowerCase().contains("jessore") ||
                segment.getName().toLowerCase().contains("bypass")) {
                baseDensity = 0.7;
            }

            // In a full implementation, we'd query: 
            // https://maps.googleapis.com/maps/api/directions/json?origin=...&destination=...&departure_time=now&key=...
            // For this live demo, we will simulate the traffic multiplier based on real-time hour
            // But the structure is ready to hit the URL above.
            
            double hourFactor = 1.0;
            int hour = LocalTime.now().getHour();
            if (hour >= 17 && hour <= 21) hourFactor = 1.4; // Peak Evening
            if (hour >= 23 || hour <= 4) hourFactor = 0.5; // Dead Night
            
            return Math.min(1.0, baseDensity * hourFactor);
        } catch (Exception e) {
            return 0.5;
        }
    }
}
