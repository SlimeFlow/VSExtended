package com.slimeflow.vsextended;

import com.slimeflow.vsextended.brush.PeakBrush;
import com.thevoxelbox.voxelsniper.VoxelBrushManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VSExtended extends JavaPlugin {
    private static VSExtended instance;

    public static VSExtended getInstance() {return VSExtended.instance;}


    @Override
    public void onEnable() {
        VSExtended.instance = this;

        VoxelBrushManager vbm = VoxelBrushManager.getInstance();
        //vbm.registerSniperBrush(SnowBrush.class, "snw", "snow");
        vbm.registerSniperBrush(PeakBrush.class, "pk", "peak");
    }
}
