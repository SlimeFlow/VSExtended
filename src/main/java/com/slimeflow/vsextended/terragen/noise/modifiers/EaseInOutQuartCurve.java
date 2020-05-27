package com.slimeflow.vsextended.terragen.noise.modifiers;

public class EaseInOutQuartCurve implements INoiseModifier {
    @Override
    public double modify(double x) {
        return x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
    }

    @Override
    public String getName() {
        return null;
    }
}
