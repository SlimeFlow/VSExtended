package com.slimeflow.vsextended;

import com.slimeflow.vsextended.brush.PeakBrush;
import com.slimeflow.vsextended.brush.SnowBrush;
import com.slimeflow.vsextended.command.VoxelExtendedBrushCommand;
import com.thevoxelbox.voxelsniper.VoxelBrushManager;
import com.thevoxelbox.voxelsniper.VoxelCommandManager;
import com.thevoxelbox.voxelsniper.VoxelSniper;
import com.thevoxelbox.voxelsniper.command.VoxelCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VSExtended extends JavaPlugin {
    private static VSExtended instance;

    public static VSExtended getInstance() {return VSExtended.instance;}


    @Override
    public void onEnable() {
        VSExtended.instance = this;

        //Injecting my commands...
        instance.getLogger().info("Injecting brush into VoxelSniper BrushManager");
        VoxelBrushManager vbm = VoxelBrushManager.getInstance();
        vbm.registerSniperBrush(PeakBrush.class, "pk", "peak");
        vbm.registerSniperBrush(SnowBrush.class, "snw", "snow");

        //Get Master plugin CommandManager
        VoxelCommandManager vcm = VoxelCommandManager.getInstance();

        instance.getLogger().info("VoxelSniper CommandManager reloading attempt for enabling autocompletion...");
        //Attepmt to access private field to enable autocompletion...
        try {

            //Accessing field
            Field privateArgumentsMapField = vcm.getClass().getDeclaredField("argumentsMap");

            //Grant access to private field... Admin override mode biatch !!
            privateArgumentsMapField.setAccessible(true);
            HashMap<String, List<String>> map = (HashMap<String, List<String>>) privateArgumentsMapField.get(vcm);

            //Destroy original map...
            map.clear();

            //Recrate it... Yes. it's ugly.
            VoxelCommandManager.initialize();


        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            instance.getLogger().info("VoxelSniper CommandManager successfully reloaded");
        }

    }
}
