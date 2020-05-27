package com.slimeflow.vsextended.math.curves;

import com.slimeflow.vsextended.math.curves.ICurve;

public class EaseOutQuartCurve implements ICurve {
    @Override
    public double evaluate(double time) {
        return 1 - Math.pow(1 - time, 4);
    }

    @Override
    public String getName() {
        return "Ease Out Quadratic";
    }
}
