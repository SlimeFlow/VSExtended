package com.slimeflow.vsextended.math;

import com.slimeflow.vsextended.math.MVector2D;
import org.bukkit.Location;
import org.bukkit.World;

public class MVector3D {
    public final int x;
    public final int y;
    public final int z;


    public MVector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public MVector3D() {
        this(0,0,0);
    }
    public MVector3D(MVector2D v, int y) {
        this(v.x(), y, v.z());
    }
    public MVector3D(MVector2D v) {
        this (v.x(), 0, v.z());
    }

    public int x() { return this.x; }
    public int y() { return this.y; }
    public int z() { return this.z; }


    public MVector2D toMVector2D() {
        return new MVector2D(this.x, this.z);
    }

    public Location toBukkitLocation(World w) {
        return new Location(w, this.x, this.y, this.z);
    }

}
