package com.safety.route;

import java.time.LocalTime;

/**
 * Interface for fetching crowd density metrics.
 * Implementations could use Google Traffic, Places API, or OSM data.
 */
public interface CrowdService {
    /**
     * Returns a factor from 0.1 (deserted) to 1.0 (very crowded).
     */
    double getCrowdDensity(RoadSegment segment, LocalTime time);
}
