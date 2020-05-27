package com.slimeflow.vsextended.brush;

import com.flowpowered.math.vector.Vector3f;
import com.google.common.collect.Lists;
import com.slimeflow.vsextended.terragen.math.vectors.Vec3i;
import com.slimeflow.vsextended.terragen.noise.CaveData;
import com.slimeflow.vsextended.terragen.noise.LandData;
import com.slimeflow.vsextended.terragen.noise.TerrainGenerator;
import com.slimeflow.vsextended.terragen.presets.CavePreset;
import com.slimeflow.vsextended.terragen.presets.LandPreset;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.stream.Collectors;

public class TerraGenBrush extends Brush {


    private boolean     showParamsInfo = false;
    private CaveData    caveData;
    private CavePreset  cavePreset;
    private LandData    landData;
    private LandPreset  landPreset;


    public TerraGenBrush(){
        super();
        this.setName("TerraGen Brush");
        this.landPreset = LandPreset.DEFAULT;
        this.landData = landPreset.getLandData();
        this.cavePreset = CavePreset.DEFAULT;
        this.caveData = cavePreset.getCaveData();

        this.landData.setSeed(42);
        this.caveData.setSeed(42);
    }

    /*
    private void generate(SnipeData v, Block targetBlock){
        Undo u = new Undo();
        int size = v.getBrushSize();

        HashMap<Vec3i, Boolean> blockMap;

        blockMap = TerrainGenerator.generateNoiseBall(new Vec3i(targetBlock), size, this.noiseData);

        for (Map.Entry<Vec3i, Boolean> entry : blockMap.entrySet()) {
            Vec3i k = entry.getKey();
            Boolean b = entry.getValue();
            if (!b){
                u.put(this.clampY(k.X(), k.Y(), k.Z()));
                setBlockMaterialAndDataAt(k.X(), k.Y(), k.Z(), v.getVoxelMaterial().createBlockData());
            }
        }
        v.owner().storeUndo(u);
    }*/

    private void generate(SnipeData v, Block targetBlock){
        Undo u = new Undo();
        int size = v.getBrushSize();

        HashMap<Vec3i, Double> blockMap = TerrainGenerator.generateTerrain(new Vec3i(targetBlock), size, this.caveData, this.landData);

        for (Map.Entry<Vec3i, Double> entry : blockMap.entrySet()){
            Vec3i k = entry.getKey();
            u.put(this.clampY(k.X(), k.Y(), k.Z()));
            if (entry.getValue() > 0) {
                setBlockMaterialAndDataAt(k.X(), k.Y(), k.Z(), v.getVoxelMaterial().createBlockData());
            } else {
                if(!v.getVoxelList().contains(getBlockMaterialAt(k.X(), k.Y(), k.Z())))
                    setBlockMaterialAndDataAt(k.X(), k.Y(), k.Z(), Material.AIR.createBlockData());
            }
        }
        v.owner().storeUndo(u);
    }


    @Override
    public void arrow(SnipeData v) {
        generate(v, this.getTargetBlock());
        super.arrow(v);
    }

    @Override
    public void powder(SnipeData v) {
        super.powder(v);
    }

    @Override
    public void info(VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        this.surfaceInfo(vm);
        this.caveInfo(vm);
        vm.custom((showParamsInfo ? ChatColor.GREEN : ChatColor.GRAY) + "Show Advanced params : " + showParamsInfo);
    }

    private void surfaceInfo(VoxelMessage vm){
        vm.custom(ChatColor.GOLD + "Surface Preset : " + ChatColor.AQUA + landPreset.name().toLowerCase());
        if (showParamsInfo) {
            vm.custom(ChatColor.GOLD + "----SURFACE Noise params----");
            vm.custom(ChatColor.BLUE + "Surface Minimum Elevation: " + landData.minElevation());
            vm.custom(ChatColor.BLUE + "Surface Maximum Elevation: " + landData.maxElevation());
            vm.custom(ChatColor.BLUE + "Surface Noise scale: " + landData.scaleX());
            vm.custom(ChatColor.BLUE + "Surface Octaves: " + landData.octaves());
            vm.custom(ChatColor.BLUE + "Surface Lacunarity: " + landData.lacunarity());
            vm.custom(ChatColor.BLUE + "Surface Gain: " + landData.gain());
            vm.custom(ChatColor.BLUE + "Surface Seed: " + landData.seed());
        }
    }

    private void caveInfo(VoxelMessage vm){
        vm.custom(ChatColor.GOLD + "Cave Preset : " + ChatColor.AQUA + cavePreset.name().toLowerCase());
        if(showParamsInfo){
            vm.custom(ChatColor.GOLD + "----CAVE Noise params----");
            caveScaleInfo(vm);
            vm.custom(ChatColor.BLUE + "Cave Air Clamp: " + caveData.airThreshold());
            vm.custom(ChatColor.BLUE + "Cave Octaves: " + caveData.octaves());
            vm.custom(ChatColor.BLUE + "Cave Lacunarity: " + caveData.lacunarity());
            vm.custom(ChatColor.BLUE + "Cave Gain: " + caveData.gain());
            vm.custom(ChatColor.BLUE + "Cave Seed: " + caveData.seed());
        }
    }

    private void caveScaleInfo(VoxelMessage vm){
        vm.custom(ChatColor.BLUE + "Cave Noise scale x: " + caveData.scaleX());
        vm.custom(ChatColor.BLUE + "Cave Noise scale z: " + caveData.scaleZ());
        vm.custom(ChatColor.BLUE + "Cave Noise scale y: " + caveData.scaleY());
    }

    @Override
    public void parseParameters(String triggerHandle, String[] params, SnipeData v)
    {

        try {
            parseInfoParams(triggerHandle, params, v);
            parseSurfaceParams(triggerHandle, params, v);
            parseCaveParams(triggerHandle, params, v);
        } catch (NumberFormatException e) {
            v.sendMessage(ChatColor.RED + "Invalid parameter! Use " + ChatColor.LIGHT_PURPLE + "'/b " + triggerHandle + " info'" + ChatColor.RED + " to display valid parameters.");
        } catch (IllegalArgumentException e) {
            v.sendMessage(ChatColor.RED + "Invalid Preset Name !");
        }
    }

    private void parseInfoParams(String triggerHandle, String[] params, SnipeData v)
    {
        if (params[0].equalsIgnoreCase("-v")){
            this.showParamsInfo = true;
            this.info(new VoxelMessage(v));
        } else if (params[0].equalsIgnoreCase("-vf")){
            this.showParamsInfo = false;
            this.info(new VoxelMessage(v));
        }else if (params[0].equalsIgnoreCase("help")){
            this.printHelp(new VoxelMessage(v));
        }
    }

    private void parseSurfaceParams(String triggerHandle, String[] params, SnipeData v) throws IllegalArgumentException
    {
        if (params[0].equalsIgnoreCase("surfacePreset")){
            LandPreset input = LandPreset.valueOf(params[1].toUpperCase());
            this.landPreset = input;
            this.landData = input.getLandData();
            this.surfaceInfo(new VoxelMessage(v));
        }
        else if (params[0].equalsIgnoreCase("surfaceScale")){
            float input = Math.abs(Float.parseFloat(params[1]));
            this.landData.setVScale(new Vector3f(input, input, input));
            v.sendMessage(ChatColor.GREEN + "Surface scale set to " + input);
        }
        else if (params[0].equalsIgnoreCase("surfaceGain")){
            float input = Float.parseFloat(params[1]);
            if (this.landData.setGain(input))
                v.sendMessage(ChatColor.GREEN + "Surface Gain set to " + this.landData.gain());
            else
                v.sendMessage(ChatColor.RED + "Gain Value must be in range [0,1] (float). Gain reset to: " + this.landData.gain());
        }
        else if (params[0].equalsIgnoreCase("surfaceLacunarity")){
            float input = Float.parseFloat(params[1]);
            this.landData.setLacunarity(input);
            v.sendMessage(ChatColor.GREEN + "Surface Lacunarity set to " + this.landData.lacunarity());
        }
        else if (params[0].equalsIgnoreCase("surfaceOctaves")){
            int input = Math.abs(Integer.parseInt(params[1]));
            this.landData.setOctaves(input);
            v.sendMessage(ChatColor.GREEN + "Surface Octaves set to " + this.landData.octaves());
        }
        else if (params[0].equalsIgnoreCase("surfaceMinElevation")){
            int input = Integer.parseInt(params[1]);
            this.landData.setMinElevation(input);
            v.sendMessage(ChatColor.GREEN + "Surface Minimum elevation set to " + this.landData.minElevation());
        }
        else if (params[0].equalsIgnoreCase("surfaceMaxElevation")){
            int input = Integer.parseInt(params[1]);
            this.landData.setMaxElevation(input);
            v.sendMessage(ChatColor.GREEN + "Surface Maximum elevation set to " + this.landData.maxElevation());
        }
    }

    private void parseCaveParams(String triggerHandle, String[] params, SnipeData v) throws IllegalArgumentException
    {
        if (params[0].equalsIgnoreCase("cavePreset")){
            CavePreset input = CavePreset.valueOf(params[1].toUpperCase());
            this.cavePreset = input;
            this.caveData = input.getCaveData();
            this.surfaceInfo(new VoxelMessage(v));
        }
        if (params[0].equalsIgnoreCase("airThreshold")){
            float input = Float.parseFloat(params[1]);
            this.caveData.setAirThreshold(input);
            v.sendMessage(ChatColor.GREEN + "Air threshold set to " + this.caveData.airThreshold());
        }
        else if (params[0].equalsIgnoreCase("caveScale")){
            float input = Math.abs(Float.parseFloat(params[1]));
            this.caveData.setVScale(new Vector3f(input, input, input));
            v.sendMessage(ChatColor.GREEN + "Cave scale set to " + input);
            this.caveScaleInfo(new VoxelMessage(v));
        }
        else if (params[0].equalsIgnoreCase("caveScaleX")){
            float input = Math.abs(Float.parseFloat(params[1]));
            float y = this.caveData.vScale().getY();
            float z = this.caveData.vScale().getZ();
            this.caveData.setVScale(new Vector3f(input, y, z));
            v.sendMessage(ChatColor.GREEN + "Cave scale X set to " + this.caveData.scaleX());
            this.caveScaleInfo(new VoxelMessage(v));
        }
        else if (params[0].equalsIgnoreCase("caveScaleY")){
            float input = Math.abs(Float.parseFloat(params[1]));
            float x = this.caveData.vScale().getX();
            float z = this.caveData.vScale().getZ();
            this.caveData.setVScale(new Vector3f(x, input, z));
            v.sendMessage(ChatColor.GREEN + "Cave scale Y set to " + this.caveData.scaleY());
            this.caveScaleInfo(new VoxelMessage(v));
        }
        else if (params[0].equalsIgnoreCase("caveScaleZ")){
            float input = Math.abs(Float.parseFloat(params[1]));
            float y = this.caveData.vScale().getY();
            float x = this.caveData.vScale().getX();
            this.caveData.setVScale(new Vector3f(x, y, input));
            v.sendMessage(ChatColor.GREEN + "Cave scale Z set to " + this.caveData.scaleZ());
            this.caveScaleInfo(new VoxelMessage(v));
        }
        else if (params[0].equalsIgnoreCase("caveGain")){
            float input = Float.parseFloat(params[1]);
            if (this.caveData.setGain(input))
                v.sendMessage(ChatColor.GREEN + "Surface Gain set to " + this.landData.gain());
            else
                v.sendMessage(ChatColor.RED + "Gain Value must be in range [0,1] (float). Gain reset to: " + this.landData.gain());
        }
        else if (params[0].equalsIgnoreCase("caveLacunarity")){
            float input = Float.parseFloat(params[1]);
            this.caveData.setLacunarity(input);
            v.sendMessage(ChatColor.GREEN + "Cave Lacunarity set to " + this.caveData.lacunarity());
        }
        else if (params[0].equalsIgnoreCase("caveOctaves")){
            int input = Math.abs(Integer.parseInt(params[1]));
            this.caveData.setOctaves(input);
            v.sendMessage(ChatColor.GREEN + "Cave Octaves set to " + this.caveData.octaves());
        }
    }

    @Override
    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap<>();


        //surface Related argument values
        argumentValues.put("surfacePreset", Arrays.stream(LandPreset.values()).map(Enum::name).collect(Collectors.toList()));
        argumentValues.put("surfaceScale", Lists.newArrayList("[number float]"));
        argumentValues.put("surfaceOctaves", Lists.newArrayList("[number int 1-8]"));
        argumentValues.put("surfaceGain", Lists.newArrayList("[number float] "));
        argumentValues.put("surfaceLacunarity", Lists.newArrayList("[number float]"));
        argumentValues.put("surfaceMinElevation", Lists.newArrayList("[number int]"));
        argumentValues.put("surfaceMaxElevation", Lists.newArrayList("[number int]"));

        //Cave Related arguments values
        argumentValues.put("cavePreset", Arrays.stream(CavePreset.values()).map(Enum::name).collect(Collectors.toList()));
        argumentValues.put("caveScale", Lists.newArrayList("[number float]"));
        argumentValues.put("caveScaleX", Lists.newArrayList("[number float]"));
        argumentValues.put("caveScaleY", Lists.newArrayList("[number float]"));
        argumentValues.put("caveScaleZ", Lists.newArrayList("[number float]"));
        argumentValues.put("caveOctaves", Lists.newArrayList("[number int 1-8]"));
        argumentValues.put("caveGain", Lists.newArrayList("[number float]"));
        argumentValues.put("caveLacunarity", Lists.newArrayList("[number float]"));
        argumentValues.put("caveAirThreshold", Lists.newArrayList("[number float]"));

        return argumentValues;
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Lists.newArrayList("cavePreset", "surfacePreset"));
        arguments.addAll(Lists.newArrayList("caveScale", "caveScaleX", "caveScaleY", "caveScaleZ", "caveOctaves", "caveGain", "caveLacunarity", "caveAirThreshold"));
        arguments.addAll(Lists.newArrayList("surfaceScale", "surfaceOctaves","surfaceGain","surfaceLacunarity","surfaceMinLevel","surfaceMaxLevel"));
        return arguments;
    }

    private void printHelp(VoxelMessage vm){
        vm.custom(ChatColor.AQUA + "TerraGen use Fractional Brownian Noise to generate blocks.");
        vm.custom(ChatColor.AQUA + "To get decent results, you can experiment with the scale, octaves, lacunarity ang gain.");
        vm.custom(ChatColor.AQUA + "Bump the "
                +ChatColor.GOLD+"octaves"
                +ChatColor.AQUA+", increase "
                +ChatColor.GOLD+"lacunarity"
                +ChatColor.AQUA+" and decrease "
                +ChatColor.GOLD+"gain"
                +ChatColor.AQUA+" to obtain a finer granularity in the noise");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.terragen";
    }
}
