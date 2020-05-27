package com.slimeflow.vsextended.terragen.presets;

import com.slimeflow.vsextended.terragen.noise.CaveData;
import com.slimeflow.vsextended.terragen.noise.LandData;

public enum LandPreset {

    DEFAULT(new LandData(200F, 8, 3F, 0.2F, 64, 150));

    private final LandData landData;

    LandPreset(LandData landData) {
        this.landData = landData;
    }


    public LandData getLandData() {
        return landData;
    }

}
