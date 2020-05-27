package com.slimeflow.vsextended.terragen.noise;

import com.flowpowered.math.vector.Vector3f;

public class LandData extends NoiseData{

    private int minElevation;
    private int maxElevation;

    public LandData(float scale, int octaves, float lacunarity, float gain, int minElevation, int maxElevation) {
        this(new Vector3f(scale,scale,scale), octaves, lacunarity, gain, minElevation, maxElevation);
    }

    public LandData(Vector3f vScale, int octaves, float lacunarity, float gain, int minElevation, int maxElevation) {
        super(vScale, octaves, lacunarity, gain);
        this.maxElevation = maxElevation;
        this.minElevation = minElevation;
    }

    public int minElevation() {
        return minElevation;
    }

    public void setMinElevation(int minElevation) {
        this.minElevation = minElevation;
    }

    public int maxElevation() {
        return maxElevation;
    }

    public void setMaxElevation(int maxElevation) {
        this.maxElevation = maxElevation;
    }
}
