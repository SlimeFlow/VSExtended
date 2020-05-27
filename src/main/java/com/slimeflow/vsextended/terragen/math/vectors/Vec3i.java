package com.slimeflow.vsextended.terragen.math.vectors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

import java.util.Objects;


public class Vec3i {

    public static final Vec3i ZERO = new Vec3i(0,0,0);
    public static final Vec3i FORWARD = new Vec3i(0,1,0);
    public static final Vec3i BACKWARD = new Vec3i(0,-1,0);
    public static final Vec3i LEFT = new Vec3i(-1,0,0);
    public static final Vec3i RIGHT = new Vec3i(1,0,0);
    public static final Vec3i UP = new Vec3i(0,0,1);
    public static final Vec3i DOWN = new Vec3i(0,0,-1);

    private final int x;
    private final int z;
    private final int y;

    public Vec3i(int x, int z, int y) { this.x = x; this.z = z; this.y = y; }
    public Vec3i(int all) { this(all, all, all); }
    public Vec3i(Block block){ this(block.getX(), block.getZ(), block.getY()); }
    public Vec3i(Vec2i v2i, int y){ this.x = v2i.X(); this.z = v2i.Z(); this.y = y; }

    public int X(){ return x; }
    public int Z(){ return z; }
    public int Y(){ return y; }

    public Vec3i add(Vec3i v){
        return new Vec3i(x + v.X(), z + v.Z(), y + v.Y());
    }
    public Vec3i add(int value){
        return new Vec3i(x + value, z + value, y + value);
    }

    @Override
    public String toString() {
        return "Vector{" + "x=" + x + ", z=" + z + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3i vec3i = (Vec3i) o;
        return x == vec3i.x &&
                z == vec3i.z &&
                y == vec3i.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, y);
    }
}
