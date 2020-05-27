package com.slimeflow.vsextended.brush;

import com.flowpowered.math.GenericMath;
import com.google.common.collect.Lists;
import com.slimeflow.vsextended.brush.presets.PeakPreset;
import com.slimeflow.vsextended.brush.presets.PeakData;
import com.slimeflow.vsextended.math.curves.EaseOutCurve;
import com.slimeflow.vsextended.math.MVector2D;
import com.slimeflow.vsextended.math.MVector3D;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.slimeflow.vsextended.terragen.noise.SimplexNoise.noise;



public class PeakBrush extends Brush {

    private PeakPreset preset = PeakPreset.DEFAULT;
    private PeakData params;

    public PeakBrush() {
        this.setName("Peak");
        params = preset.createData();
    }

    private void addPeak(final SnipeData v, Block targetBlock, boolean inverted) {
        final Undo u = new Undo();

        int bSize = v.getBrushSize();

        MVector2D vRoot = new MVector2D(targetBlock.getX(), targetBlock.getZ());
        MVector2D vMin  = new MVector2D(GenericMath.floor(targetBlock.getX() - bSize), GenericMath.floor(targetBlock.getZ() - bSize));
        MVector2D vMax  = new MVector2D(GenericMath.floor(targetBlock.getX() + bSize), GenericMath.floor(targetBlock.getZ() + bSize));
        double maxDist  = vRoot.distance(vMin);

        for (int x = vMin.x(); x <= vMax.x(); x++) {
            for (int z = vMin.z(); z <= vMax.z(); z++) {

                // Get elevation by noise
                double noise = getSimplexNoiseForPos(x, z);
                int elevation = map(noise, -1, 1, 0, this.params.getElevation());

                // ease elevation
                if (this.params.getEasing() > 0) {
                    double revertedNormalizedDistance = 1 - (vRoot.distance(x, z) / maxDist);
                    EaseOutCurve curve = new EaseOutCurve(this.params.getEasing());
                    double easeOut = curve.evaluate(revertedNormalizedDistance);
                    elevation = GenericMath.floor(elevation * easeOut);
                }

                // generate blocs
                MVector3D pos = new MVector3D(x, targetBlock.getY(), z);
                generateElevationAtPos(pos, elevation, u, v.getVoxelMaterial(), inverted, v);
            }
        }
        v.owner().storeUndo(u);

    }

    private int map(double value, double targetMin, double targetMax, double vMin, double vMax) {

        double s = vMin + (value - targetMin) * (vMax - vMin) / (targetMax - targetMin);
        return GenericMath.floor(s);

    }

    private double getSimplexNoiseForPos(int x, int z) {
        double sampleX = (x / this.params.getNoiseScale()) + this.params.getSeed();
        double sampleZ = (z / this.params.getNoiseScale()) + this.params.getSeed();

        return noise(sampleX, sampleZ);
    }

    private void generateElevationAtPos(MVector3D pos, int elevation, Undo u, Material m, boolean inverted, SnipeData v) {
        if (elevation >= 1) {
            if (inverted) {
                for (int i = 0; i <= elevation; i++) {
                    int currY = pos.y() - i;
                    u.put(this.clampY(pos.x(), currY, pos.z()));
                    setBlockMaterialAndDataAt(pos.x(), currY, pos.z(), m.createBlockData());
                }
                for (int i = 1; i <= this.params.getDepth(); i++){
                    int depth = pos.y() + i;
                    Material mat = getBlockMaterialAt(pos.x(), depth, pos.z());
                    if (mat == Material.VOID_AIR || mat == Material.AIR) {
                        u.put(this.clampY(pos.x(), depth, pos.z()));
                        setBlockMaterialAndDataAt(pos.x(), depth, pos.z(), m.createBlockData());
                    }
                }
            } else {
                for (int i = 0; i <= elevation; i++) {
                    int currY = pos.y() + i;
                    u.put(this.clampY(pos.x(), currY, pos.z()));
                    setBlockMaterialAndDataAt(pos.x(), currY, pos.z(), m.createBlockData());
                }
                for (int i = 1; i <= this.params.getDepth(); i++){
                    int depth = pos.y() - i;
                    Material mat = getBlockMaterialAt(pos.x(), depth, pos.z());
                    if (mat == Material.VOID_AIR || mat == Material.AIR) {
                        u.put(this.clampY(pos.x(), depth, pos.z()));
                        setBlockMaterialAndDataAt(pos.x(), depth, pos.z(), m.createBlockData());
                    }
                }
            }
        }
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
        vm.custom(ChatColor.GREEN + "Noise Scale (noiseScale): " + this.params.getNoiseScale());
        vm.custom(ChatColor.GREEN + "Elevation (elevation): " + this.params.getElevation());
        vm.custom(ChatColor.GREEN + "Ease Factor (ease): " + this.params.getEasing());
        vm.custom(ChatColor.GOLD + "Fill Depth (depth): " + this.params.getDepth());
        vm.custom(ChatColor.GOLD + "Seed (seed): " + this.params.getSeed());
        vm.custom(ChatColor.BLUE + "Preset :  " + this.preset.name());
        vm.size();
    }


    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.peak";
    }

    @Override
    public void parseParameters(String triggerHandle, String[] params, SnipeData v) {

        //preset param...
        if (params[0].equalsIgnoreCase("preset")){
            try {
                PeakPreset presetInput = PeakPreset.valueOf(params[1].toUpperCase());
                this.preset = presetInput;
                this.params = presetInput.createData();
                this.info(new VoxelMessage(v));
            } catch (IllegalArgumentException e) {
                v.sendMessage(ChatColor.RED + "Invalid Preset Name !");
            }
        }

        try {
            //Noise scale Param...
            if (params[0].equalsIgnoreCase("noiseScale")){
                double noiseScaleInput = Double.parseDouble(params[1]);

                if (this.params.validateNoiseScale(noiseScaleInput)) {
                    this.params.setNoiseScale(noiseScaleInput);
                } else {
                    this.params.setNoiseScale(1);
                    v.sendMessage(ChatColor.RED + "Noise scale must be at least 1.");
                }
                v.sendMessage(ChatColor.GREEN + "Noise Scale set to " + this.params.getNoiseScale());
            }
            //Easing Param...
            if (params[0].equalsIgnoreCase("ease")){
                int easeInput = Integer.parseInt(params[1]);

                if (easeInput < 0) {
                    this.params.setEasing(0);
                    v.sendMessage(ChatColor.RED + "Ease disabled ! You cant set a value below 0.");
                } else {
                    if (easeInput == 0) {
                        this.params.setEasing(easeInput);
                        v.sendMessage(ChatColor.GREEN + "Ease disabled");
                    } else {
                        this.params.setEasing(easeInput);
                        v.sendMessage(ChatColor.GREEN + "Ease set to " + this.params.getEasing());
                    }
                }


            }

            //Elevation Param...
            if (params[0].equalsIgnoreCase("elevation")){
                int elevationIpt = Integer.parseInt(params[1]);
                this.params.setElevation(elevationIpt);
                v.sendMessage(ChatColor.GREEN + "Elevation set to " + this.params.getElevation());
            }

            if (params[0].equalsIgnoreCase("seed")){
                int seedInput = Integer.parseInt(params[1]);
                this.params.setSeed(seedInput);
                v.sendMessage(ChatColor.GREEN + "Seed set to " + this.params.getSeed());
            }

            if (params[0].equalsIgnoreCase("depth")){
                int depthInput = Integer.parseInt(params[1]);
                this.params.setDepth(depthInput);
                v.sendMessage(ChatColor.GREEN + "Filling depth set to " + this.params.getDepth());
            }
        } catch (NumberFormatException e) {
            v.sendMessage(ChatColor.RED + "Invalid parameter! Use " + ChatColor.LIGHT_PURPLE + "'/b " + triggerHandle + " info'" + ChatColor.RED + " to display valid parameters.");
        }
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.addAll(Lists.newArrayList("noiseScale", "elevation", "ease", "depth", "seed", "preset"));

        return arguments;
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();


        argumentValues.put("noiseScale", Lists.newArrayList("[number]"));
        argumentValues.put("elevation", Lists.newArrayList("[number]"));
        argumentValues.put("ease", Lists.newArrayList("[number]"));
        argumentValues.put("depth", Lists.newArrayList("[number]"));
        argumentValues.put("seed", Lists.newArrayList("[number]"));
        argumentValues.put("preset", Arrays.stream(PeakPreset.values()).map(e -> e.name()).collect(Collectors.toList()));


        argumentValues.putAll(super.registerArgumentValues());
        return argumentValues;
    }


}
