package com.slimeflow.vsextended.terragen.noise;

import com.flowpowered.math.vector.Vector3f;

public class CaveData extends NoiseData{
    private float airThreshold;

    public CaveData(float scale, int octaves, float lacunarity, float gain, float airThreshold) {
        super(scale, octaves, lacunarity, gain);
        this.airThreshold = airThreshold;
    }

    public CaveData(Vector3f vScale, int octaves, float lacunarity, float gain, float airThreshold) {
        super(vScale, octaves, lacunarity, gain);
        this.airThreshold = airThreshold;
    }


    public float airThreshold() {
        return airThreshold;
    }

    public void setAirThreshold(float airThreshold) {
        this.airThreshold = airThreshold;
    }
}
