package com.slimeflow.vsextended.terragen.noise.modifiers;

public interface INoiseModifier {

    double modify(double noiseValue);
    String getName();

}
