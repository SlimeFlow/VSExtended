package com.slimeflow.vsextended.terragen.math.vectors;

import org.bukkit.block.Block;

import java.util.Objects;

public class Vec2d {

    public static final Vec2d ZERO = new Vec2d(0,0);
    public static final Vec2d FORWARD = new Vec2d(0,1);
    public static final Vec2d BACKWARD = new Vec2d(0,-1);
    public static final Vec2d LEFT = new Vec2d(-1,0);
    public static final Vec2d RIGHT = new Vec2d(1,0);

    private final double x;
    private final double z;

    public Vec2d(double x, double z) { this.x = x; this.z = z; }
    public Vec2d(double all) { this(all, all); }
    public Vec2d(Block block){ this(block.getX(), block.getZ()); }

    public double X(){ return x; }
    public double Z(){ return z; }

    public Vec2d add(Vec2d v){
        return new Vec2d(x + v.X(), z + v.Z());
    }
    public Vec2d add(int value){
        return new Vec2d(x + value, z + value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2d vec2d = (Vec2d) o;
        return Double.compare(vec2d.x, x) == 0 &&
                Double.compare(vec2d.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
