package com.slimeflow.vsextended.terragen.noise;

import com.flowpowered.math.vector.Vector3f;

public abstract class NoiseData {

    private int seed;
    private Vector3f vScale;
    private int octaves;
    private float lacunarity;
    private float gain;

    public NoiseData(float scale, int octaves, float lacunarity, float gain) {
        this(new Vector3f(scale, scale, scale), octaves, lacunarity, gain);
    }

    public NoiseData(Vector3f vScale, int octaves, float lacunarity, float gain) {
        this.seed = 151;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain = gain;
        this.vScale = vScale;
    }

    public int octaves() {
        return octaves;
    }

    public void setOctaves(int octaves) {
        this.octaves = octaves;
    }

    public float lacunarity() {
        return lacunarity;
    }

    public void setLacunarity(float lacunarity) {
        this.lacunarity = lacunarity;
    }

    public int seed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public Vector3f vScale() { return vScale; }

    public void setVScale(Vector3f vScale) { this.vScale = vScale; }

    public float scaleX() {return vScale.getX();}

    public float scaleZ() {return vScale.getZ();}

    public float scaleY() {return vScale.getY();}

    public float gain() {
        return gain;
    }

    public boolean setGain(float gain) {
        if (gain < 0 || gain > 1)
        {
            this.gain = 0.1F;
            return false;
        }
        this.gain = gain;
        return true;
    }
}
