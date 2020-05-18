package com.slimeflow.vsextended.brush;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.slimeflow.vsextended.utils.SimplexNoise.noise;

public class PeakBrush extends Brush {

    private final int SEED_DEFAULT = 0;
    private int seed = SEED_DEFAULT;

    private final int EASE_FACTOR_DEFAULT = 5;
    private int easeFactor = EASE_FACTOR_DEFAULT;

    private final int ELEVATION_MAX_DEFAULT = 30;
    private int elevationMax = ELEVATION_MAX_DEFAULT;

    private final double NOISE_SCALE_DEFAULT = 20;
    private double noiseScale = NOISE_SCALE_DEFAULT;

    public PeakBrush() {
        this.setName("Peak");
    }

    private void addPeak(final SnipeData v, Block targetBlock, boolean inverted) {
        final Undo u = new Undo();

        int bSize = v.getBrushSize();
        int minX = GenericMath.floor(targetBlock.getX() - bSize);
        int maxX = GenericMath.floor(targetBlock.getX() + bSize);
        int minZ = GenericMath.floor(targetBlock.getZ() - bSize);
        int maxZ = GenericMath.floor(targetBlock.getZ() + bSize);
        int rootX = targetBlock.getX();
        int rootZ = targetBlock.getZ();

        double maxDistance = getBlockDistance(rootX, rootZ, minX, minZ);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                double noise = getSimplexNoiseForPos(x, z);
                int elevation = map(noise, -1, 1, 0, this.elevationMax);

                double normalizedBlockDistance = getBlockDistance(rootX, rootZ, x, z) / maxDistance;
                double revertedDistance = 1 - normalizedBlockDistance;
                double easeMultiplier = ease(revertedDistance);

                int easedElevation = GenericMath.floor(elevation * easeMultiplier);
                generateElevationAtPos(x, targetBlock.getY(), z, easedElevation, u, v.getVoxelMaterial(), inverted);
            }
        }
        v.owner().storeUndo(u);

    }

    private int map(double value, double targetMin, double targetMax, double vMin, double vMax) {

        double s = vMin + (value - targetMin) * (vMax - vMin) / (targetMax - targetMin);
        return GenericMath.floor(s);

    }

    private double getSimplexNoiseForPos(int x, int z) {
        double sampleX = (x / this.noiseScale) + this.seed;
        double sampleZ = (z / this.noiseScale) + this.seed;

        return noise(sampleX, sampleZ);
    }

    private void generateElevationAtPos(int x, int y, int z, int elevation, Undo u, Material m, boolean inverted) {
        if (elevation >= 1) {
            if (inverted) {
                for (int i = 0; i <= elevation; i++) {
                    int currY = y - i;
                    u.put(this.clampY(x, currY, z));
                    setBlockMaterialAndDataAt(x, currY, z, m.createBlockData());
                }
            } else {
                for (int i = 0; i <= elevation; i++) {
                    int currY = y + i;
                    u.put(this.clampY(x, currY, z));
                    setBlockMaterialAndDataAt(x, currY, z, m.createBlockData());

                    int underY = currY - 1;
                    while (getBlockMaterialAt(x, underY, z) == Material.AIR) {
                        setBlockMaterialAndDataAt(x, underY, z, m.createBlockData());
                        u.put(this.clampY(x, underY, z));
                        underY--;
                    }
                }
            }
        }
    }

    private double getBlockDistance(int x1, int z1, int x2, int z2) {

        return Math.sqrt((x2-x1)*(x2-x1) + (z2-z1)*(z2-z1));

    }

    private double ease(double value) {
        return (Math.pow(value, this.easeFactor + 1) / (Math.pow(value,2) + (1 - value)));
    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.addPeak(v, this.getTargetBlock(), false);
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.addPeak(v, this.getLastBlock(), true);
    }

    @Override
    public void info(VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.custom(ChatColor.GREEN + "Noise Scale (sc): " + this.noiseScale);
        vm.custom(ChatColor.GREEN + "Elevation (el): " + this.elevationMax);
        vm.custom(ChatColor.GREEN + "Ease Factor (ef): " + this.easeFactor);
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.peak";
    }

    @Override
    public void parseParameters(String triggerHandle, String[] params, SnipeData v) {

        try {
            if (params[0].equalsIgnoreCase("sc")){
                double noiseScaleInput = Double.parseDouble(params[1]);
                if (noiseScaleInput < NOISE_SCALE_DEFAULT) {
                    noiseScaleInput = NOISE_SCALE_DEFAULT;
                }
                this.noiseScale = noiseScaleInput;
                v.sendMessage(ChatColor.GREEN + "Noise Scale set to " + this.noiseScale);
            }
            if (params[0].equalsIgnoreCase("ef")){
                int easeInput = Integer.parseInt(params[1]);
                if (easeInput < 0) {
                    easeInput = EASE_FACTOR_DEFAULT;
                }
                this.easeFactor = easeInput;
                v.sendMessage(ChatColor.GREEN + "Ease set to " + this.easeFactor);
            }
            if (params[0].equalsIgnoreCase("el")){
                int elevationIpt = Integer.parseInt(params[1]);
                if (elevationIpt < 5) {
                    elevationIpt = ELEVATION_MAX_DEFAULT;
                }
                this.elevationMax = elevationIpt;
                v.sendMessage(ChatColor.GREEN + "Elevation set to " + this.elevationMax);
            }
            if (params[0].equalsIgnoreCase("seed")){
                int seedIpt = Integer.parseInt(params[1]);

                this.seed = seedIpt;
                v.sendMessage(ChatColor.GREEN + "Seed set to " + this.seed);
            }
        } catch (NumberFormatException e) {
            v.sendMessage(ChatColor.RED + "Invalid parameter! Use " + ChatColor.LIGHT_PURPLE + "'/b " + triggerHandle + " info'" + ChatColor.RED + " to display valid parameters.");
        }
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.addAll(Lists.newArrayList("sc", "el", "ef", "seed"));

        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();


        argumentValues.put("sc", Lists.newArrayList("[number]"));
        argumentValues.put("el", Lists.newArrayList("[number]"));
        argumentValues.put("ef", Lists.newArrayList("[number]"));
        argumentValues.put("seed", Lists.newArrayList("[number]"));

        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }


}
