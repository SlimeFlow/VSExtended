package com.slimeflow.vsextended.terragen.noise.modifiers;

import com.slimeflow.vsextended.terragen.math.vectors.Vec2d;

public class CubicBezierCurve implements INoiseModifier {

    private Vec2d p0 = new Vec2d(0,0);
    private Vec2d p3 = new Vec2d(1,1);
    private Vec2d p1 = new Vec2d(1.313, -0.026);
    private Vec2d p2 = new Vec2d(0.656, 0.94);

    @Override
    public double modify(double t) {
        double y = (1-t)*(1-t)*(1-t)*p0.Z() + 3*(1-t)*(1-t)*t*p1.Z() + 3*(1-t)*t*t*p2.Z() + t*t*t*p3.Z();
        return y;
    }

    @Override
    public String getName() {
        return "Cubic Bezier Curve";
    }



}
