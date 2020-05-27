package com.slimeflow.vsextended.terragen.presets;

import com.slimeflow.vsextended.terragen.noise.CaveData;
import com.slimeflow.vsextended.terragen.noise.NoiseData;

public enum CavePreset {

    DEFAULT                         (new CaveData(40f, 3, 3f, 0.1f, 1f)),
    CAVITIES_LARGE_SMOOTH           (new CaveData(50f, 3, 0.1f, 0.5f, 1.5f)),
    CAVITIES_LARGE_RIFT             (new CaveData(60f, 3, 3f, 0.2f, 1f)),
    CAVITIES_BUBBLES_XXL            (new CaveData(100f, 3, 1f, 0.5f, 1.8f)),
    CAVITIES_BUBBLES_METABALL       (new CaveData(50f, 3, 1f, 0.5f, 1.8f)),
    CAVITIES_NOISY                  (new CaveData(20f, 3, 0.1f, 0.5f, 1.5f)),
    CAVITIES_EXPLODED_SMALLBLOBS    (new CaveData(20f, 3, 2f, 0.5f, 1.8f)),
    CAVITIES_EXPLODED_LARGEBLOBS    (new CaveData(100f, 3, 5f, 0.5f, 1.8f)),
    CAVITIES_SPONGE                 (new CaveData(20f, 3, 1f, 0.5f, 1.5f)),
    CAVITIES_MAZE                   (new CaveData(50f, 3, 2f, 2f, 1.2f)),
    CAVITIES_ERODED_GROTTO          (new CaveData(40f, 3, 3f, 0.1f, 1f));

    private final CaveData caveData;

    CavePreset(CaveData caveData) {
        this.caveData = caveData;
    }


    public CaveData getCaveData() {
        return caveData;
    }


}
