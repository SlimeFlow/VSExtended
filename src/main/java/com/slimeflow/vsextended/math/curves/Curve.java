package com.slimeflow.vsextended.math.curves;

public enum Curve {
    EASE_OUT_QUADRATIC(new EaseOutQuartCurve()),
    EASE_OUT(new EaseOutCurve(5));

    private final ICurve curve;

    Curve(ICurve curve) {
        this.curve = curve;
    }

    public ICurve getCurve() {
        return curve;
    }
}
