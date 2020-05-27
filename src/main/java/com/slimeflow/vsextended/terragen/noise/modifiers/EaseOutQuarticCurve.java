package com.slimeflow.vsextended.terragen.noise.modifiers;

public class EaseOutQuarticCurve implements INoiseModifier{
    @Override
    public double modify(double noiseValue) {
        return 1 - Math.pow((0.5 - noiseValue / 2), 4) * 2;
    }

    @Override
    public String getName() {
        return "Erosion Quartic";
    }
}
