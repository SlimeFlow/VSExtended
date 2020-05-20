package com.slimeflow.vsextended.brush.presets;

public class PeakData extends BrushData {

    private int seed;
    private int elevation;
    private double noiseScale;
    private int easing;
    private int depth;

    public PeakData(int seed, int elevation, double noiseScale, int easing, int depth) {
        super("PeakPreset");
        this.seed = seed;
        this.elevation = elevation;
        this.noiseScale = noiseScale;
        this.easing = easing;
        this.depth = depth;
    }


    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public double getNoiseScale() {
        return noiseScale;
    }

    public void setNoiseScale(double noiseScale) {
        this.noiseScale = noiseScale;
    }

    public int getEasing() {
        return easing;
    }

    public void setEasing(int easing) {
        this.easing = easing;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean validateNoiseScale(double value) {
        return value >= 0;
    }
}
