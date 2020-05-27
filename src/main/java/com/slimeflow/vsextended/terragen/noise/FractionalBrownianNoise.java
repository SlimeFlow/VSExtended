package com.slimeflow.vsextended.terragen.noise;

import com.flowpowered.math.GenericMath;
import com.slimeflow.vsextended.terragen.noise.modifiers.*;

import java.awt.*;
import java.awt.geom.CubicCurve2D;

public class FractionalBrownianNoise {

    private final NoiseData params;
    private float distortionStrength = 0;

    private double minimumNoiseResult = Double.MAX_VALUE;
    private double maximumNoiseResult = Double.MIN_VALUE;

    public FractionalBrownianNoise(NoiseData params){
        this.params = params;
    }

    public Double get2DNoise(int x, int z){

        double amplitude = 1;
        double frequency = 1;
        double noiseResult = 0;

        for (int o = 0; o < params.octaves(); o++) {
            double sampleX = x / params.scaleX() * frequency;
            double sampleZ = z / params.scaleZ() * frequency;

            //Getting noise to range [0,1]
            double noiseValue = (SimplexNoise.noise(sampleX, sampleZ) + 1) / 2;

            //Applying Curve Modification
            //...

            //Get back to a value in range [-1,1]
            noiseValue = noiseValue * 2 - 1;
            noiseResult += noiseValue * amplitude;
            amplitude *= params.gain();
            frequency *= params.lacunarity();
        }

        if (noiseResult > maximumNoiseResult)
            maximumNoiseResult = noiseResult;
        else if (noiseResult < minimumNoiseResult)
            minimumNoiseResult = noiseResult;

        return noiseResult;
    }

    public Double get3DNoise(int x, int z, int y){
        double amplitude = 1;
        double frequency = 1;
        double noiseResult = 0;

        for (int o = 0; o < params.octaves(); o++) {

            double sampleX = x / params.scaleX() * frequency;
            double sampleZ = z / params.scaleZ() * frequency;
            double sampleY = y / params.scaleY() * frequency;

            double noiseValue;

            if (this.distortionStrength > 0){
                noiseValue = DistortedNoise(sampleX, sampleZ, sampleY, this.distortionStrength);
            } else {
                noiseValue = SimplexNoise.noise(sampleX, sampleZ, sampleY);
            }

            noiseResult += noiseValue * amplitude;

            amplitude *= params.gain();
            frequency *= params.lacunarity();

        }

        if (noiseResult > maximumNoiseResult)
            maximumNoiseResult = noiseResult;
        else if (noiseResult < minimumNoiseResult)
            minimumNoiseResult = noiseResult;

        return noiseResult;
    }

    private double DistortedNoise(double x, double z, double y, float strength) {

        double xDist = strength * Distort(x + 2.3d, z + 2.9d, y + 3.4d);
        double zDist = strength * Distort(x - 3.6d, z - 4.3d, y + 4.8d);
        double yDist = strength * Distort(x + 5.5d, z + 5.9d, y + 6.3d);

        return SimplexNoise.noise(x + xDist, z + zDist, y + yDist);
    }

    private double Distort(double x, double z, double y){
        float density = 4.7f;
        return SimplexNoise.noise(x * density, z * density, y * density);
    }

    public Double getMaximumNoiseResult() {
        return maximumNoiseResult;
    }

    public Double getMinimumNoiseResult() {
        return minimumNoiseResult;
    }

    public void setDistortionStrength(float distortionStrength) {
        this.distortionStrength = distortionStrength;
    }

    public float getDistortionStrength() {
        return distortionStrength;
    }

    private double normalize(double value, double targetMin, double targetMax, double vMin, double vMax) {


        return vMin + (value - targetMin) * (vMax - vMin) / (targetMax - targetMin);

    }
}
