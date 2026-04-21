public record RoadNode(String id, double latitude, double longitude) {
    public double lat() { return latitude; }
    public double lon() { return longitude; }
    
    @Override
    public String toString() {
        return id;
    }
}
