package com.slimeflow.vsextended.brush.presets;

public enum PeakPreset {

    DEFAULT(0, 30, 20, 5, 3),
    MOUNTAIN_PEAK(0, 30, 10, 5, 5),
    TERRAIN_BILLOWY(0, 25, 20, 0, 15),
    TERRAIN_NOISY(0, 25, 2, 0, 15);

    private final int seed;
    private final int elevation;
    private final double noiseScale;
    private final int ease;
    private final int depth;

    PeakPreset(int seed, int elevation, double noiseScale, int ease, int depth) {
        this.seed = seed;
        this.elevation = elevation;
        this.noiseScale = noiseScale;
        this.ease = ease;
        this.depth = depth;
    }

    public PeakData createData() {
        return new PeakData(seed, elevation, noiseScale, ease, depth);
    }
}
