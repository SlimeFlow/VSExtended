package com.slimeflow.vsextended.terragen.math.vectors;

public class Vec3d {
    public static final Vec3d ZERO = new Vec3d(0,0,0);

    private final double x;
    private final double z;
    private final double y;

    public Vec3d(double x, double z, double y) { this.x = x; this.z = z; this.y = y; }
    public Vec3d(double all) { this(all, all, all); }

    public double X(){ return x; }
    public double Z(){ return z; }
    public double Y(){ return y; }

    public Vec3d add(Vec3d v){
        return new Vec3d(x + v.X(), z + v.Z(), y + v.Y());
    }
    public Vec3d add(double value){
        return new Vec3d(x + value, z + value, y + value);
    }
}
