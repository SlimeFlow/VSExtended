package com.slimeflow.vsextended.math;

import org.bukkit.Location;
import org.bukkit.World;

public class MVector2D {

    public static final MVector2D ONE = new MVector2D(1,1);
    public static final MVector2D ZERO = new MVector2D(0,0);

    private final int x;
    private final int z;

    /***
     * Minecraft uses X and Z to represent the 2D plane axis coordinates. And Y for up.
     *
     * @param x Block position on X axis
     * @param z Block position on Z axis
     */
    public MVector2D(int x, int z) {
        this.x = x;
        this.z = z;
    }
    public MVector2D() { this(0,0); }
    public MVector2D(MVector3D v){ this(v.x, v.z); }


    public int x() { return this.x; }
    public int z() { return this.z; }

    /***
     * Calculate this distance between this vector coordinates and a raw set of coordinates
     * @param x Block position on X axis
     * @param z Block position on Z axis
     * @return vector distance as a double
     */
    public double distance(int x, int z) {
        return Math.sqrt(this.distanceSquared(x, z));
    }

    /***
     * Calculate this distance between this vector and another
     *
     * @param v Vector to calculate the distance from.
     * @return vector distance as a double
     */
    public double distance(MVector2D v) {
        return this.distance(v.x, v.z);
    }


    /***
     * Calculate this distance squared between this vector coordinates and a raw set of coordinates
     * @param x Block position on X axis
     * @param z Block position on Z axis
     * @return squared distance as a double
     */
    public double distanceSquared(int x, int z) {
        double dx = this.x - x;
        double dz = this.z - z;
        return dx * dx + dz * dz;
    }

    /***
     * Calculate this squared distance between this vector and another
     *
     * @param v Vector to calculate the distance from.
     * @return vector distance as a double
     */
    public double distanceSquared(MVector2D v) {
        return this.distanceSquared(v.x, v.z);
    }

    /***
     * Calculate the length squared of this vector
     * @return length as double
     */
    public double lengthSquared() {
        return this.x * this.x + this.z * this.z;
    }

    /***
     * Calculate the length of this vector
     * @return length as double
     */
    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    /**
     * Instantiate a new negative version of this vector
     * @return a new MVector2D, negated
     */
    public MVector2D negate() {
        return new MVector2D(-this.x, -this.z);
    }


    public MVector2D add(int x, int z) {
        return new MVector2D(this.x + x, this.z + z);
    }

    public MVector2D add(int value) {
        return this.add(value, value);
    }

    public MVector2D add(MVector2D v) {
        return this.add(this.x + v.x(), this.z + v.z());
    }

    /***
     * Convert to a MVector3D
     * @param y the Missing Y coordinate
     * @return MVector3D
     */
    public MVector3D toMVector3D(int y) {
        return new MVector3D(this.x, y, this.z);
    }

    /***
     * Convert to a MVector3D with 0 on Y axis
     * @return MVector3D
     */
    public MVector3D toMVector3D() {
        return new MVector3D(this.x, 0, this.z);
    }

    // Bukkit Location Conversion

    /***
     * Covert this vector into a Bukkit Location.
     * @param y The Y axis missing from this vector 2D
     * @param w a valid world
     * @return a bukkit Location object
     */
    public Location toBukkitLocation(World w, double y) {
        return new Location(w, this.x, y, this.z);
    }

    /***
     * Convert this Vector into a Bukkit location and let you decide if you want the find the highest block
     * @param w a valid Bukkit World
     * @param highestBlock if false, Y = 0
     * @return a Bukkit Location object
     */
    public Location toBukkitLocation(World w, boolean highestBlock) {
        if (highestBlock) {
            double y = w.getHighestBlockYAt(this.x, this.z);
            return this.toBukkitLocation(w, y);
        } else {
            return this.toBukkitLocation(w, 0);
        }
    }

    /***
     * Covert this vector into a Bukkit Location with the highest block on Y axis.
     * @param w a valid world
     * @return a bukkit Location object
     */
    public Location toBukkitLocation(World w) {
        return this.toBukkitLocation(w, true);
    }

}
