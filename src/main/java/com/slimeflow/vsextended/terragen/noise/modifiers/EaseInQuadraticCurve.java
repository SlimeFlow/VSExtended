package com.slimeflow.vsextended.terragen.noise.modifiers;

public class EaseInQuadraticCurve implements INoiseModifier {
    @Override
    public double modify(double noiseValue) {
        return Math.pow(0.5 + noiseValue / 2, 2) * 2 - 1;
    }

    @Override
    public String getName() {
        return "Ease In Quadratic";
    }
}
