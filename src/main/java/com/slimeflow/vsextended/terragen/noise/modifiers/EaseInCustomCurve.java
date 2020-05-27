package com.slimeflow.vsextended.terragen.noise.modifiers;

public class EaseInCustomCurve implements INoiseModifier {

    private int power = 2;

    @Override
    public double modify(double noiseValue) {
        return Math.pow(0.5 + noiseValue / 2, this.power) * 2 - 1;
    }

    @Override
    public String getName() {
        return "Erosion Quadratic";
    }

    public void setPower(int power) {
        if (power < 2)
            this.power = 2;
        else
            this.power = power;
    }
}
