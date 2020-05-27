package com.slimeflow.vsextended.terragen.noise.modifiers;

public class InLinearCurve implements INoiseModifier {

    @Override
    public double modify(double noiseValue) {
        return 1 - Math.abs(0.5 + noiseValue / 2) * 2;
    }

    @Override
    public String getName() {
        return "Linear Erosion";
    }
}
