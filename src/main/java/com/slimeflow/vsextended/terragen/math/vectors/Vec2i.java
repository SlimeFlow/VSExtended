package com.slimeflow.vsextended.terragen.math.vectors;

import org.bukkit.block.Block;

import java.util.Objects;

public class Vec2i {

    public static final Vec2i ZERO = new Vec2i(0,0);
    public static final Vec2i FORWARD = new Vec2i(0,1);
    public static final Vec2i BACKWARD = new Vec2i(0,-1);
    public static final Vec2i LEFT = new Vec2i(-1,0);
    public static final Vec2i RIGHT = new Vec2i(1,0);

    private final int x;
    private final int z;

    public Vec2i(int x, int z) { this.x = x; this.z = z; }
    public Vec2i(int all) { this(all, all); }
    public Vec2i(Block block){ this(block.getX(), block.getZ()); }

    public int X(){ return x; }
    public int Z(){ return z; }

    public Vec2i add(Vec2i v){
        return new Vec2i(x + v.X(), z + v.Z());
    }
    public Vec2i add(int value){
        return new Vec2i(x + value, z + value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2i vec2i = (Vec2i) o;
        return x == vec2i.x &&
                z == vec2i.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
