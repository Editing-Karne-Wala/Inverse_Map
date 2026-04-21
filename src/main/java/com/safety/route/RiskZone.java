package com.safety.route;

public record RiskZone(String name, double latitude, double longitude, double radiusMeters, double riskMultiplier) {
    public boolean isInside(double lat, double lon) {
        // Simple haversine distance or approximate Euclidean for MVP
        double dLat = Math.toRadians(lat - latitude);
        double dLon = Math.toRadians(lon - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(lat)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371000 * c; // Earth radius in meters
        return distance <= radiusMeters;
    }
}
