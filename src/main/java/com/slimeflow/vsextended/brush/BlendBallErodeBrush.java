package com.slimeflow.vsextended.brush;

import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.libs.com.google.common.base.Objects;
import com.thevoxelbox.voxelsniper.libs.com.google.common.collect.Lists;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.Entry;

public class BlendBallErodeBrush extends Brush {

    private static final Vector[] FACES_TO_CHECK = new Vector[]{new Vector(0, 0, 1), new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(0, -1, 0), new Vector(1, 0, 0), new Vector(-1, 0, 0)};
    private String presetName = "NONE";
    private BlendBallErodeBrush.ErosionPreset currentPreset = new BlendBallErodeBrush.ErosionPreset(0, 1, 0, 1);

    private boolean excludeAir;
    private boolean excludeWater;

    public BlendBallErodeBrush() {
        this.setName("Blend Ball Erode");
    }


    @Override
    protected void arrow(SnipeData v) {
        this.erosion(v, this.currentPreset);
        this.excludeAir = false;
        blend(v);
    }

    @Override
    protected void powder(SnipeData v) {
        this.erosion(v, this.currentPreset.getInverted());
        this.excludeAir = true;
        blend(v);
    }

    @SuppressWarnings("deprecation")
    private void erosion(final SnipeData v, final ErosionPreset erosionPreset) {
        final BlockChangeTracker blockChangeTracker = new BlockChangeTracker(this.getTargetBlock().getWorld());

        final Vector targetBlockVector = this.getTargetBlock().getLocation().toVector();

        for (int i = 0; i < erosionPreset.getErosionRecursion(); ++i) {
            erosionIteration(v, erosionPreset, blockChangeTracker, targetBlockVector);
        }

        for (int i = 0; i < erosionPreset.getFillRecursion(); ++i) {
            fillIteration(v, erosionPreset, blockChangeTracker, targetBlockVector);
        }

        final Undo undo = new Undo();
        for (final BlockWrapper blockWrapper : blockChangeTracker.getAll()) {
            undo.put(blockWrapper.getBlock());
            blockWrapper.getBlock().setBlockData(blockWrapper.getBlockData(), true);
        }

        v.owner().storeUndo(undo);
    }

    private void erosionIteration(final SnipeData v, final ErosionPreset erosionPreset, final BlockChangeTracker blockChangeTracker, final Vector targetBlockVector) {
        final int currentIteration = blockChangeTracker.nextIteration();
        for (int x = this.getTargetBlock().getX() - v.getBrushSize(); x <= this.getTargetBlock().getX() + v.getBrushSize(); ++x) {
            for (int z = this.getTargetBlock().getZ() - v.getBrushSize(); z <= this.getTargetBlock().getZ() + v.getBrushSize(); ++z) {
                for (int y = this.getTargetBlock().getY() - v.getBrushSize(); y <= this.getTargetBlock().getY() + v.getBrushSize(); ++y) {
                    final Vector currentPosition = new Vector(x, y, z);
                    if (currentPosition.isInSphere(targetBlockVector, v.getBrushSize())) {
                        final BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);

                        if (currentBlock.isEmpty() || currentBlock.isLiquid()) {
                            continue;
                        }

                        int count = 0;
                        for (final Vector vector : BlendBallErodeBrush.FACES_TO_CHECK) {
                            final Vector relativePosition = currentPosition.clone().add(vector);
                            final BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);

                            if (relativeBlock.isEmpty() || relativeBlock.isLiquid()) {
                                count++;
                            }
                        }

                        if (count >= erosionPreset.getErosionFaces()) {
                            blockChangeTracker.put(currentPosition, new BlockWrapper(currentBlock.getBlock(), Material.AIR), currentIteration);
                        }
                    }
                }
            }
        }
    }

    private void fillIteration(final SnipeData v, final ErosionPreset erosionPreset, final BlockChangeTracker blockChangeTracker, final Vector targetBlockVector) {
        final int currentIteration = blockChangeTracker.nextIteration();
        for (int x = this.getTargetBlock().getX() - v.getBrushSize(); x <= this.getTargetBlock().getX() + v.getBrushSize(); ++x) {
            for (int z = this.getTargetBlock().getZ() - v.getBrushSize(); z <= this.getTargetBlock().getZ() + v.getBrushSize(); ++z) {
                for (int y = this.getTargetBlock().getY() - v.getBrushSize(); y <= this.getTargetBlock().getY() + v.getBrushSize(); ++y) {
                    final Vector currentPosition = new Vector(x, y, z);
                    if (currentPosition.isInSphere(targetBlockVector, v.getBrushSize())) {
                        final BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);

                        if (!(currentBlock.isEmpty() || currentBlock.isLiquid())) {
                            continue;
                        }

                        int count = 0;

                        final Map<BlockWrapper, Integer> blockCount = new HashMap<BlockWrapper, Integer>();

                        for (final Vector vector : BlendBallErodeBrush.FACES_TO_CHECK) {
                            final Vector relativePosition = currentPosition.clone().add(vector);
                            final BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);

                            if (!(relativeBlock.isEmpty() || relativeBlock.isLiquid())) {
                                count++;
                                final BlockWrapper typeBlock = new BlockWrapper(null, relativeBlock.getMaterial());
                                if (blockCount.containsKey(typeBlock)) {
                                    blockCount.put(typeBlock, blockCount.get(typeBlock) + 1);
                                } else {
                                    blockCount.put(typeBlock, 1);
                                }
                            }
                        }

                        BlockWrapper currentMaterial = new BlockWrapper(null, Material.AIR);
                        int amount = 0;

                        for (final BlockWrapper wrapper : blockCount.keySet()) {
                            final Integer currentCount = blockCount.get(wrapper);
                            if (amount <= currentCount) {
                                currentMaterial = wrapper;
                                amount = currentCount;
                            }
                        }

                        if (count >= erosionPreset.getFillFaces()) {
                            blockChangeTracker.put(currentPosition, new BlockWrapper(currentBlock.getBlock(), currentMaterial.getMaterial()), currentIteration);
                        }
                    }
                }
            }
        }
    }

    protected final void blend(SnipeData v) {
        final int brushSize = v.getBrushSize();
        final int brushSizeDoubled = 2 * brushSize;
        // Array that holds the original materials plus a buffer
        final Material[][][] oldMaterials = new Material[2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1][2 * (brushSize + 1) + 1];
        // Array that holds the blended materials
        final Material[][][] newMaterials = new Material[brushSizeDoubled + 1][brushSizeDoubled + 1][brushSizeDoubled + 1];

        // Log current materials into oldmats
        for (int x = 0; x <= 2 * (brushSize + 1); x++) {
            for (int y = 0; y <= 2 * (brushSize + 1); y++) {
                for (int z = 0; z <= 2 * (brushSize + 1); z++) {
                    oldMaterials[x][y][z] = this.getBlockMaterialAt(this.getTargetBlock().getX() - brushSize - 1 + x, this.getTargetBlock().getY() - brushSize - 1 + y, this.getTargetBlock().getZ() - brushSize - 1 + z);
                }
            }
        }

        // Log current materials into newmats
        for (int x = 0; x <= brushSizeDoubled; x++) {
            for (int y = 0; y <= brushSizeDoubled; y++) {
                for (int z = 0; z <= brushSizeDoubled; z++) {
                    newMaterials[x][y][z] = oldMaterials[x + 1][y + 1][z + 1];
                }
            }
        }

        // Blend materials
        for (int x = 0; x <= brushSizeDoubled; x++) {
            for (int y = 0; y <= brushSizeDoubled; y++) {
                for (int z = 0; z <= brushSizeDoubled; z++) {
                    Map<Material, Integer> materialFrequency = new HashMap<>();

                    boolean tiecheck = true;

                    for (int m = -1; m <= 1; m++) {
                        for (int n = -1; n <= 1; n++) {
                            for (int o = -1; o <= 1; o++) {
                                if (!(m == 0 && n == 0 && o == 0)) {
                                    Material currentMaterial = oldMaterials[x + 1 + m][y + 1 + n][z + 1 + o];
                                    int currentFrequency = materialFrequency.getOrDefault(currentMaterial, 0) + 1;

                                    materialFrequency.put(currentMaterial, currentFrequency);
                                }
                            }
                        }
                    }

                    int highestMaterialCount = 0;
                    Material highestMaterial = Material.AIR;

                    // Find most common neighbouring material
                    for (Entry<Material, Integer> e : materialFrequency.entrySet()) {
                        if (e.getValue() > highestMaterialCount && !(this.excludeAir && e.getKey() == Material.AIR) && !(this.excludeWater && e.getKey() == Material.WATER)) {
                            highestMaterialCount = e.getValue();
                            highestMaterial = e.getKey();
                        }
                    }

                    // Make sure that there's no tie in highest material
                    for (Entry<Material, Integer> e : materialFrequency.entrySet()) {
                        if (e.getValue() == highestMaterialCount && !(this.excludeAir && e.getKey() == Material.AIR) && !(this.excludeWater && e.getKey() == Material.WATER)) {
                            if (e.getKey() == highestMaterial) {
                                continue;
                            }
                            tiecheck = false;
                        }
                    }

                    // Record most common neighbor material for this block
                    if (tiecheck) {
                        newMaterials[x][y][z] = highestMaterial;
                    }
                }
            }
        }

        final Undo undo = new Undo();
        final double rSquared = Math.pow(brushSize + 1, 2);

        // Make the changes
        for (int x = brushSizeDoubled; x >= 0; x--) {
            final double xSquared = Math.pow(x - brushSize - 1, 2);

            for (int y = 0; y <= brushSizeDoubled; y++) {
                final double ySquared = Math.pow(y - brushSize - 1, 2);

                for (int z = brushSizeDoubled; z >= 0; z--) {
                    if (xSquared + ySquared + Math.pow(z - brushSize - 1, 2) <= rSquared) {
                        if (!(this.excludeAir && newMaterials[x][y][z] == Material.AIR) && !(this.excludeWater && (newMaterials[x][y][z] == Material.WATER))) {
                            if (this.getBlockMaterialAt(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + y, this.getTargetBlock().getZ() - brushSize + z) != newMaterials[x][y][z]) {
                                undo.put(this.clampY(this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + y, this.getTargetBlock().getZ() - brushSize + z));
                            }
                            this.setBlockMaterialAt(this.getTargetBlock().getZ() - brushSize + z, this.getTargetBlock().getX() - brushSize + x, this.getTargetBlock().getY() - brushSize + y, newMaterials[x][y][z]);
                        }
                    }
                }
            }
        }
        v.owner().storeUndo(undo);
    }

    @Override
    public void info(VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
        vm.voxel();
        vm.custom(ChatColor.BLUE + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
        vm.custom(ChatColor.GOLD + "Active brush preset is " + ChatColor.YELLOW + this.presetName + ChatColor.GOLD + ".");
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.erode";
    }

    public final void parseParameters(String triggerHandle, String[] params, SnipeData v) {

        if (params[0].equalsIgnoreCase("water")) {
            this.excludeWater = !this.excludeWater;
            v.sendMessage(ChatColor.AQUA + "Water Mode: " + (this.excludeWater ? "exclude" : "include"));
        }
        if (params[0].equalsIgnoreCase("info")) {
            v.sendMessage(ChatColor.GOLD + "Erode Brush Parameters:");
            v.sendMessage(ChatColor.AQUA + "/b " + triggerHandle + " [preset]  -- Change active erode brush preset");
        }
        if (params[0].equalsIgnoreCase("preset")){
            try {
                BlendBallErodeBrush.Preset preset = BlendBallErodeBrush.Preset.valueOf(params[1].toUpperCase());
                this.currentPreset = preset.getPreset();
                this.presetName = preset.name();
                v.sendMessage(ChatColor.GOLD + "Brush preset changed to " + ChatColor.YELLOW + this.presetName + ChatColor.GOLD + ".");
            } catch (IllegalArgumentException var5) {
                v.getVoxelMessage().brushMessage(ChatColor.RED + "That preset does not exist.");
            }

        }
    }

    @Override
    public List<String> registerArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.addAll(Lists.newArrayList("water", "preset"));

        return arguments;
    }


    public HashMap<String, List<String>> registerArgumentValues() {
        HashMap<String, List<String>> argumentValues = new HashMap();
        argumentValues.put("water", Lists.newArrayList("true", "false"));
        argumentValues.put("preset", Arrays.stream(BlendBallErodeBrush.Preset.values()).map(Enum::name).collect(Collectors.toList()));

        return argumentValues;
    }



    private static final class ErosionPreset {
        private final int erosionFaces;
        private final int erosionRecursion;
        private final int fillFaces;
        private final int fillRecursion;

        public ErosionPreset(int erosionFaces, int erosionRecursion, int fillFaces, int fillRecursion) {
            this.erosionFaces = erosionFaces;
            this.erosionRecursion = erosionRecursion;
            this.fillFaces = fillFaces;
            this.fillRecursion = fillRecursion;
        }

        public int hashCode() {
            return Objects.hashCode(this.erosionFaces, this.erosionRecursion, this.fillFaces, this.fillRecursion);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof BlendBallErodeBrush.ErosionPreset)) {
                return false;
            } else {
                BlendBallErodeBrush.ErosionPreset other = (BlendBallErodeBrush.ErosionPreset)obj;
                return Objects.equal(this.erosionFaces, other.erosionFaces) && Objects.equal(this.erosionRecursion, other.erosionRecursion) && Objects.equal(this.fillFaces, other.fillFaces) && Objects.equal(this.fillRecursion, other.fillRecursion);
            }
        }

        public int getErosionFaces() {
            return this.erosionFaces;
        }

        public int getErosionRecursion() {
            return this.erosionRecursion;
        }

        public int getFillFaces() {
            return this.fillFaces;
        }

        public int getFillRecursion() {
            return this.fillRecursion;
        }

        public BlendBallErodeBrush.ErosionPreset getInverted() {
            return new BlendBallErodeBrush.ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
        }
    }

    private static final class BlockWrapper {
        private final Block block;
        private final BlockData blockData;

        public BlockWrapper(Block block) {
            this.block = block;
            this.blockData = block.getBlockData();
        }

        public BlockWrapper(Block block, Material material) {
            this.block = block;
            this.blockData = material.createBlockData();
        }

        public Block getBlock() {
            return this.block;
        }

        public BlockData getBlockData() {
            return this.blockData;
        }

        public Material getMaterial() {
            return this.blockData.getMaterial();
        }

        public boolean isEmpty() {
            return this.getMaterial() == Material.AIR;
        }

        public boolean isLiquid() {
            switch(this.getMaterial()) {
                case WATER:
                case LAVA:
                    return true;
                default:
                    return false;
            }
        }
    }

    private static final class BlockChangeTracker {
        private final Map<Integer, Map<Vector, BlendBallErodeBrush.BlockWrapper>> blockChanges = new HashMap();
        private final Map<Vector, BlendBallErodeBrush.BlockWrapper> flatChanges = new HashMap();
        private final World world;
        private int nextIterationId = 0;

        public BlockChangeTracker(World world) {
            this.world = world;
        }

        public BlendBallErodeBrush.BlockWrapper get(Vector position, int iteration) {
            BlendBallErodeBrush.BlockWrapper changedBlock = null;

            for(int i = iteration - 1; i >= 0; --i) {
                if (this.blockChanges.containsKey(i) && this.blockChanges.get(i).containsKey(position)) {
                    changedBlock = (BlendBallErodeBrush.BlockWrapper)((Map)this.blockChanges.get(i)).get(position);
                    return changedBlock;
                }
            }

            changedBlock = new BlendBallErodeBrush.BlockWrapper(position.toLocation(this.world).getBlock());
            return changedBlock;
        }

        public Collection<BlendBallErodeBrush.BlockWrapper> getAll() {
            return this.flatChanges.values();
        }

        public int nextIteration() {
            return this.nextIterationId++;
        }

        public void put(Vector position, BlendBallErodeBrush.BlockWrapper changedBlock, int iteration) {
            if (!this.blockChanges.containsKey(iteration)) {
                this.blockChanges.put(iteration, new HashMap());
            }

            ((Map)this.blockChanges.get(iteration)).put(position, changedBlock);
            this.flatChanges.put(position, changedBlock);
        }
    }
    private enum Preset {
        NONE(new BlendBallErodeBrush.ErosionPreset(0, 1, 0, 1)),
        MELT(new BlendBallErodeBrush.ErosionPreset(2, 1, 5, 1)),
        FILL(new BlendBallErodeBrush.ErosionPreset(5, 1, 2, 1)),
        SMOOTH(new BlendBallErodeBrush.ErosionPreset(3, 1, 3, 1)),
        LIFT(new BlendBallErodeBrush.ErosionPreset(6, 0, 1, 1)),
        FLOATCLEAN(new BlendBallErodeBrush.ErosionPreset(6, 1, 6, 1));

        private final BlendBallErodeBrush.ErosionPreset preset;

        Preset(BlendBallErodeBrush.ErosionPreset preset) {
            this.preset = preset;
        }

        public static String getValuesString(String seperator) {
            String valuesString = "";
            boolean delimiterHelper = true;
            BlendBallErodeBrush.Preset[] var3 = values();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                BlendBallErodeBrush.Preset preset = var3[var5];
                if (delimiterHelper) {
                    delimiterHelper = false;
                } else {
                    valuesString = valuesString + seperator;
                }

                valuesString = valuesString + preset.name();
            }

            return valuesString;
        }

        public BlendBallErodeBrush.ErosionPreset getPreset() {
            return this.preset;
        }
    }
}
