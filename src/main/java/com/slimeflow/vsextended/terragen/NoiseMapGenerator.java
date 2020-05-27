package com.slimeflow.vsextended.terragen;

import com.slimeflow.vsextended.math.MVector2D;
import com.slimeflow.vsextended.math.MVector3D;
import com.slimeflow.vsextended.terragen.noise.NoiseData;
import com.slimeflow.vsextended.terragen.noise.SimplexNoise;

import java.util.HashMap;

public class NoiseMapGenerator {

    public static long lastTiming = 0;

    public static float[][] generateNoisePlane(MVector2D size, NoiseData data) {
        final float[][] map = new float[size.x()][size.z()];

        for (int z = 0; z < size.z(); z++){
            for (int x = 0; x < size.x(); x++) {
                float sampleX = x / data.scaleX();
                float sampleZ = z / data.scaleZ();

                float noiseValue = (float) SimplexNoise.noise(sampleX, sampleZ);
                map[x][z] = noiseValue;
            }
        }

        return map;
    }

    public static  HashMap<MVector3D, Double> generatePreciseNoiseMapBall(MVector3D root, int size, NoiseData data){
        HashMap<MVector3D, Double> noiseMap = new HashMap<>();

        final double sizeSquared = Math.pow(size, 2);
        int xMin = root.x() - size;
        int xMax = root.x() + size;
        int zMin = root.z() - size;
        int zMax = root.z() + size;
        int yMin = root.y() - size;
        int yMax = root.y() + size;

        for(int z = zMin + 1; z <= zMax; z++) {
            final double zSquared = Math.pow(root.z() - z, 2);

            for (int x = xMin + 1; x <= xMax; x++) {
                final double xSquared = Math.pow(root.x() - x, 2);

                for (int y = yMin + 1; y <= yMax; y++){
                    final double ySquared = Math.pow(root.y() - y, 2);

                    if ((xSquared + ySquared + zSquared) <= sizeSquared){

                        double amplitude = 1;
                        double frequency = 1;
                        double noiseHeight = 0;

                        for (int o = 0; o < data.octaves(); o++) {
                            double sampleX = x / data.scaleX() * frequency;
                            double sampleZ = z / data.scaleZ() * frequency;
                            double sampleY = y / data.scaleY() * frequency;

                            double noiseValue = SimplexNoise.noise(sampleX, sampleZ, sampleY);

                            noiseHeight += noiseValue * amplitude;


                            amplitude = amplitude * data.gain();
                            frequency = frequency * data.lacunarity();
                        }

                        noiseMap.put(new MVector3D(x, y, z), 1 + noiseHeight);
                    }
                }
            }
        }

        return noiseMap;
    }


    public static  HashMap<MVector3D, Double> generateNormalizedPreciseNoiseMapBall(MVector3D root, int size, NoiseData data){
        HashMap<MVector3D, Double> noiseMap = new HashMap<>();

        final double sizeSquared = Math.pow(size, 2);
        int xMin = root.x() - size;
        int xMax = root.x() + size;
        int zMin = root.z() - size;
        int zMax = root.z() + size;
        int yMin = root.y() - size;
        int yMax = root.y() + size;

        double maxNoiseHeight = Double.MIN_VALUE;
        double minNoiseHeight = Double.MAX_VALUE;

        for(int z = zMin + 1; z <= zMax; z++) {
            final double zSquared = Math.pow(root.z() - z, 2);

            for (int x = xMin + 1; x <= xMax; x++) {
                final double xSquared = Math.pow(root.x() - x, 2);

                for (int y = yMin + 1; y <= yMax; y++){
                    final double ySquared = Math.pow(root.y() - y, 2);

                    if ((xSquared + ySquared + zSquared) <= sizeSquared){

                        double amplitude = 1;
                        double frequency = 1;
                        double noiseHeight = 0;

                        for (int o = 0; o < data.octaves(); o++) {
                            double sampleX = x / data.scaleX() * frequency;
                            double sampleZ = z / data.scaleZ() * frequency;
                            double sampleY = y / data.scaleY() * frequency;

                            double noiseValue = SimplexNoise.noise(sampleX, sampleZ, sampleY);

                            noiseHeight += noiseValue * amplitude;


                            amplitude = amplitude * data.gain();
                            frequency = frequency * data.lacunarity();
                        }

                        if (noiseHeight > maxNoiseHeight) {
                            maxNoiseHeight = noiseHeight;
                        } else if (noiseHeight < minNoiseHeight) {
                            minNoiseHeight = noiseHeight;
                        }

                        noiseMap.put(new MVector3D(x, y, z), noiseHeight);
                    }
                }
            }
        }

        final double vMin = minNoiseHeight;
        final double vMax = maxNoiseHeight;

        noiseMap.forEach((k, v) -> {
            noiseMap.put(k, normalize(v, vMin, vMax));
        });

        return noiseMap;
    }

    public static HashMap<MVector3D, Float> generateNoiseMapBall(MVector3D root, int size, NoiseData data){
        HashMap<MVector3D, Float> noiseMap = new HashMap<>();
        long time = System.currentTimeMillis();
        final double sizeSquared = Math.pow(size, 2);

        int xMin = root.x() - size;
        int xMax = root.x() + size;
        int zMin = root.z() - size;
        int zMax = root.z() + size;
        int yMin = root.y() - size;
        int yMax = root.y() + size;


        for(int z = zMin + 1; z <= zMax; z++) {
            final double zSquared = Math.pow(root.z() - z, 2);

            for (int x = xMin + 1; x <= xMax; x++) {
                final double xSquared = Math.pow(root.x() - x, 2);

                for (int y = yMin + 1; y <= yMax; y++){
                    final double ySquared = Math.pow(root.y() - y, 2);

                    if ((xSquared + ySquared + zSquared) <= sizeSquared){

                        float amplitude = 1;
                        float frequency = 1;
                        float noiseHeight = 0;

                        for (int o = 0; o < data.octaves(); o++) {
                            float sampleX = x / data.scaleX() * frequency;
                            float sampleZ = z / data.scaleZ() * frequency;
                            float sampleY = y / data.scaleY() * frequency;

                            float noiseValue = (float) SimplexNoise.noise(sampleX, sampleZ, sampleY);

                            noiseHeight += noiseValue * amplitude;


                            amplitude = amplitude * data.gain();
                            frequency = frequency * data.lacunarity();
                        }


                        noiseMap.put(new MVector3D(x, y, z), 1 + noiseHeight);
                    }
                }
            }
        }

        lastTiming = System.currentTimeMillis() - time;
        return noiseMap;
    }

    private static double inverseLerp(double a, double b, double value) {
        if (a != b)
            return clamp01((value - a) / (b -a));
        else
            return 0;
    }

    public static double clamp01(double value)
    {
        if (value < 0)
            return 0;
        else if (value > 1)
            return 1;
        else
            return value;
    }

    private static double normalize(double v, double vMin, double vMax){
        return (v - vMin) / (vMax - vMin);
    }

}
