package com.slimeflow.vsextended.brush;

import com.flowpowered.math.GenericMath;
import com.thevoxelbox.voxelsniper.VoxelMessage;
import com.thevoxelbox.voxelsniper.brush.perform.PerformerBrush;
import com.thevoxelbox.voxelsniper.snipe.SnipeData;
import com.thevoxelbox.voxelsniper.snipe.Undo;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

import java.util.Optional;

public class SnowBrush extends PerformerBrush {

    public SnowBrush() {
        this.setName("Snow");
    }

    private void addSnow(final SnipeData v, Block targetBlock) {
        final int brushSize = v.getBrushSize();
        final double brushSizeSquared = brushSize * brushSize;
        //this.currentPerformer.perform(targetBlock);

        int tx = targetBlock.getX();
        int tz = targetBlock.getZ();

        int minX = GenericMath.floor(targetBlock.getX() - brushSize);
        int maxX = GenericMath.floor(targetBlock.getX() + brushSize) + 1;
        int minY = Math.max(GenericMath.floor(targetBlock.getY() - brushSize), 0);
        int maxY = Math.min(GenericMath.floor(targetBlock.getY() + brushSize) + 1, 254);
        int minZ = GenericMath.floor(targetBlock.getZ() - brushSize);
        int maxZ = GenericMath.floor(targetBlock.getZ() + brushSize) + 1;

        final Undo undo = new Undo();

        for (int x = minX; x <= maxX; x++){
            double xs = (tx -x) * (tx -x);
            for (int z = minZ; z <= maxZ; z++) {
                double zs = (tz - z) * (tz -z);
                if (xs + zs < brushSizeSquared) {
                    int y = maxY;
                    boolean topFound = false;

                    for (; y >= minY; y--) {
                        if (this.getWorld().getBlockAt(x, y, z).getType() != Material.AIR) {
                            topFound = true;
                            break;
                        }
                    }

                    if (topFound) {
                        if (y == maxY) {
                            Material above = this.getWorld().getBlockAt(x, y+1, z).getType();
                            if (above != Material.AIR) {
                                continue;
                            }
                        }
                        BlockState block = this.getWorld().getBlockAt(x, y, z).getState();
                        if (block.getBlockData() instanceof Snow) {
                            boolean createNew = true;
                            Snow snowLayer = ((Snow) block.getBlockData());
                            int snowLayerCount = snowLayer.getLayers();

                            BlockData[] neighborhood = new BlockData[4];
                            neighborhood[0] = getBlockDataAt(x+1, y, z);
                            neighborhood[1] = getBlockDataAt(x-1, y, z);
                            neighborhood[2] = getBlockDataAt(x, y, z+1);
                            neighborhood[3] = getBlockDataAt(x, y, z-1);

                            for (BlockData b: neighborhood) {
                                if (b instanceof Snow) {
                                    if (snowLayerCount - ((Snow)b).getLayers() >= 3 ){
                                        createNew = false;
                                        break;
                                    }
                                } else if (b.getMaterial() == Material.AIR) {
                                    createNew = false;
                                    break;
                                }
                            }

                            if (createNew) {
                                BlockData newSnowLayer = Material.SNOW.createBlockData();
                                if (snowLayerCount == snowLayer.getMaximumLayers()) {
                                    undo.put(this.clampY(x, y, z));
                                    undo.put(this.clampY(x, y+1, z));
                                    this.setBlockMaterialAndDataAt(x, y, z, Material.SNOW_BLOCK.createBlockData());
                                    this.setBlockMaterialAndDataAt(x, y + 1, z, newSnowLayer);
                                } else {
                                    ((Snow)newSnowLayer).setLayers(snowLayerCount+1);
                                    undo.put(this.clampY(x, y, z));
                                    this.setBlockMaterialAndDataAt(x, y, z, newSnowLayer);
                                }
                            }
                        } else if (block.getType() == Material.WATER) {
                            undo.put(this.clampY(x, y, z));
                            this.setBlockMaterialAndDataAt(x, y, z, Material.ICE.createBlockData());
                        } else {
                            if (block.getType().isSolid()) {
                                undo.put(this.clampY(x, y + 1, z));
                                this.setBlockMaterialAndDataAt(x,y + 1, z, Material.SNOW.createBlockData());
                            } else if (block.getBlock().isPassable() && !block.getBlock().isLiquid()){
                                undo.put(this.clampY(x, y, z));
                                this.setBlockMaterialAndDataAt(x, y, z, Material.SNOW.createBlockData());
                            }
                        }
                    }
                }
            }
        }

        v.owner().storeUndo(undo);

    }

    @Override
    protected final void arrow(final SnipeData v) {
        this.addSnow(v, this.getTargetBlock());
    }

    @Override
    protected final void powder(final SnipeData v) {
        this.addSnow(v, this.getLastBlock());
    }

    @Override
    public void info(VoxelMessage vm) {
        vm.brushName(this.getName());
        vm.size();
    }

    @Override
    public String getPermissionNode() {
        return "voxelsniper.brush.snow";
    }
}