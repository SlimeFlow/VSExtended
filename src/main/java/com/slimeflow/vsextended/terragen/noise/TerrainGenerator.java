package com.slimeflow.vsextended.terragen.noise;

import com.flowpowered.math.GenericMath;
import com.slimeflow.vsextended.terragen.math.vectors.Vec2i;
import com.slimeflow.vsextended.terragen.math.vectors.Vec3i;
import org.bukkit.Chunk;

import java.util.*;

public class TerrainGenerator {


    public static HashMap<Vec3i, Boolean> generateNoiseChunk(Chunk chunk, NoiseData data){

        FractionalBrownianNoise fbm = new FractionalBrownianNoise(data);
        Vec3i root = new Vec3i(chunk.getBlock(0,0,0));

        return null;
    }

    public static HashMap<Vec3i, Double> generateTerrain(Vec3i root, int radius, CaveData cavity, LandData surface) {
        HashMap<Vec3i, Double> noiseMap = new HashMap<>();

        //Generate surface map, work as a guide to limit the height
        FractionalBrownianNoise surfaceNoise = new FractionalBrownianNoise(surface);
        FractionalBrownianNoise cavityNoise = new FractionalBrownianNoise(cavity);

        Vec2i vMin = new Vec2i(root.X() - radius, root.Z() - radius);
        Vec2i vMax = new Vec2i(root.X() + radius, root.Z() + radius);


        for (int x = vMin.X(); x <= vMax.X(); x++) {
            for (int z = vMin.Z(); z < vMax.Z(); z++) {
                double noiseSurface = surfaceNoise.get2DNoise(x, z);
                int surfaceY = noiseToWorld(noiseSurface, -1, 1, 64, 150);
                for (int y = 10; y < surfaceY; y++){
                    double noiseCave = 1 + cavityNoise.get3DNoise(x, z, y);
                    noiseMap.put(new Vec3i(x, z, y), noiseCave < cavity.airThreshold() ? 1D : 0D);
                }
            }
        }

        //noiseMap.putAll(surfaceSet.stream().collect(Collectors.toMap(Function.identity(), v -> 1D)));
        //Generate cave map

        return noiseMap;
    }

    public static HashMap<Vec3i, Double> generateTerrainBall(Vec3i root, int radius, CaveData cavity, LandData surface) {
        HashMap<Vec3i, Double> noiseMap = new HashMap<>();

        //Generate surface map, work as a guide to limit the height
        FractionalBrownianNoise surfaceNoise = new FractionalBrownianNoise(surface);
        int length = radius * 2 + 1;
        HashSet<Vec3i> surfaceSet = new HashSet<>();

        Vec2i vMin = new Vec2i(root.X() - radius, root.Z() - radius);
        Vec2i vMax = new Vec2i(root.X() + radius, root.Z() + radius);

        HashMap<Vec2i, Double> noiseSetRaw = new HashMap<>();
        for (int x = vMin.X(); x <= vMax.X(); x++) {
            for (int z = vMin.Z(); z < vMax.Z(); z++) {
                double d = surfaceNoise.get2DNoise(x, z);
                noiseSetRaw.put(new Vec2i(x, z), d);
            }
        }

        for (Map.Entry<Vec2i, Double> entry : noiseSetRaw.entrySet()) {
            int y = noiseToWorld(entry.getValue(), -1, 1, surface.minElevation(), surface.maxElevation());
            surfaceSet.add(new Vec3i(entry.getKey(), y));
        }

        //noiseMap.putAll(surfaceSet.stream().collect(Collectors.toMap(Function.identity(), v -> 1D)));
        //Generate cave map

        for (int x = vMin.X(); x <= vMax.X(); x++) {
            for (int z = vMin.Z(); z < vMax.Z(); z++) {
                double d = surfaceNoise.get2DNoise(x, z);
                noiseSetRaw.put(new Vec2i(x, z), d);
            }
        }


        return noiseMap;
    }

    private static int noiseToWorld(double value, double targetMin, double targetMax, double vMin, double vMax) {

        double s = vMin + (value - targetMin) * (vMax - vMin) / (targetMax - targetMin);
        return GenericMath.floor(s);

    }


    public static HashMap<Vec3i, Boolean> generateNoiseBall(Vec3i root, int radius, CaveData data){

        HashMap<Vec3i, Boolean> noiseMap = new HashMap<>();
        FractionalBrownianNoise fbn = new FractionalBrownianNoise(data);

        final double radiusPow2 = radius * radius;
        int xMin = root.X() - radius;
        int xMax = root.X() + radius;
        int zMin = root.Z() - radius;
        int zMax = root.Z() + radius;
        int yMin = root.Y() - radius;
        int yMax = root.Y() + radius;

        for(int z = zMin + 1; z <= zMax; z++) {
            final double zSquared = Math.pow(root.Z() - z, 2);

            for (int x = xMin + 1; x <= xMax; x++) {
                final double xSquared = Math.pow(root.X() - x, 2);

                for (int y = yMin + 1; y <= yMax; y++){
                    final double ySquared = Math.pow(root.Y() - y, 2);

                    if ((xSquared + ySquared + zSquared) <= radiusPow2){
                        double noiseValue = 1 + fbn.get3DNoise(x, z, y);
                        noiseMap.put(new Vec3i(x, z, y), noiseValue < data.airThreshold() );
                    }
                }
            }
        }

        return noiseMap;
    }

    public static HashMap<Vec3i, Boolean> generateNoiseDisc(Vec3i root, int radius, CaveData data){
        HashMap<Vec3i, Boolean> noiseMap = new HashMap<>();
        FractionalBrownianNoise fbn = new FractionalBrownianNoise(data);



        final double radiusPow2 = radius * radius;
        int xMin = root.X() - radius;
        int xMax = root.X() + radius;
        int zMin = root.Z() - radius;
        int zMax = root.Z() + radius;
        int y = root.Y()+1;

        for(int z = zMin + 1; z <= zMax; z++) {
            final double zSquared = Math.pow(root.Z() - z, 2);

            for (int x = xMin + 1; x <= xMax; x++) {
                final double xSquared = Math.pow(root.X() - x, 2);

                if ((xSquared + zSquared) <= radiusPow2){
                    double noiseValue = 1 + fbn.get3DNoise(x, z, y);
                    noiseMap.put(new Vec3i(x, z, y), noiseValue < data.airThreshold() );
                }
            }
        }
        return noiseMap;
    }
}
