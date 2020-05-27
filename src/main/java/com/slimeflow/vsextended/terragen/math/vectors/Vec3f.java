package com.slimeflow.vsextended.terragen.math.vectors;

public class Vec3f {

    public static final Vec3f ZERO = new Vec3f(0,0,0);

    private final float x;
    private final float z;
    private final float y;

    public Vec3f(float x, float z, float y) { this.x = x; this.z = z; this.y = y; }
    public Vec3f(float all) { this(all, all, all); }

    public float X(){ return x; }
    public float Z(){ return z; }
    public float Y(){ return y; }

    public Vec3f add(Vec3f v){
        return new Vec3f(x + v.X(), z + v.Z(), y + v.Y());
    }
    public Vec3f add(float value){
        return new Vec3f(x + value, z + value, y + value);
    }
}
