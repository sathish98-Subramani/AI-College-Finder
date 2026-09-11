package com.collegefinder.util;

import java.util.Map;

/**
 * Approximate CITY-CENTER coordinates for map display only. These are public
 * geographic facts about Indian cities (not campus-exact, not part of the
 * college dataset) used purely to place markers on the map view.
 */
public final class CityCoordinates {

    private CityCoordinates() {}

    private static final Map<String, double[]> COORDS = Map.ofEntries(
            Map.entry("bengaluru", new double[]{12.9716, 77.5946}),
            Map.entry("bhubaneswar", new double[]{20.2961, 85.8245}),
            Map.entry("chennai", new double[]{13.0827, 80.2707}),
            Map.entry("coimbatore", new double[]{11.0168, 76.9558}),
            Map.entry("dehradun", new double[]{30.3165, 78.0322}),
            Map.entry("delhi", new double[]{28.6139, 77.2090}),
            Map.entry("durgapur", new double[]{23.5204, 87.3119}),
            Map.entry("gandhinagar", new double[]{23.2156, 72.6369}),
            Map.entry("gorakhpur", new double[]{26.7606, 83.3732}),
            Map.entry("guwahati", new double[]{26.1445, 91.7362}),
            Map.entry("howrah", new double[]{22.5958, 88.2636}),
            Map.entry("hyderabad", new double[]{17.3850, 78.4867}),
            Map.entry("indore", new double[]{22.7196, 75.8577}),
            Map.entry("jaipur", new double[]{26.9124, 75.7873}),
            Map.entry("jalandhar", new double[]{31.3260, 75.5762}),
            Map.entry("jammu", new double[]{32.7266, 74.8570}),
            Map.entry("kalavakkam", new double[]{12.7996, 80.2209}),
            Map.entry("kanpur", new double[]{26.4499, 80.3319}),
            Map.entry("kharagpur", new double[]{22.3460, 87.2320}),
            Map.entry("manipal", new double[]{13.3467, 74.7869}),
            Map.entry("mumbai", new double[]{19.0760, 72.8777}),
            Map.entry("nagpur", new double[]{21.1458, 79.0882}),
            Map.entry("new delhi", new double[]{28.6139, 77.2090}),
            Map.entry("palakkad", new double[]{10.7867, 76.6548}),
            Map.entry("patiala", new double[]{30.3398, 76.3869}),
            Map.entry("patna", new double[]{25.5941, 85.1376}),
            Map.entry("phagwara", new double[]{31.2240, 75.7708}),
            Map.entry("pilani", new double[]{28.3670, 75.5960}),
            Map.entry("prayagraj", new double[]{25.4358, 81.8463}),
            Map.entry("pune", new double[]{18.5204, 73.8567}),
            Map.entry("ranchi", new double[]{23.3441, 85.3096}),
            Map.entry("roorkee", new double[]{29.8543, 77.8880}),
            Map.entry("rourkela", new double[]{22.2604, 84.8536}),
            Map.entry("silchar", new double[]{24.8333, 92.7789}),
            Map.entry("surat", new double[]{21.1702, 72.8311}),
            Map.entry("thanjavur", new double[]{10.7870, 79.1378}),
            Map.entry("thiruvananthapuram", new double[]{8.5241, 76.9366}),
            Map.entry("tiruchirappalli", new double[]{10.7905, 78.7047}),
            Map.entry("tirupati", new double[]{13.6288, 79.4192}),
            Map.entry("varanasi", new double[]{25.3176, 82.9739})
    );

    public static double[] lookup(String city) {
        if (city == null) return null;
        return COORDS.get(city.trim().toLowerCase());
    }
}
