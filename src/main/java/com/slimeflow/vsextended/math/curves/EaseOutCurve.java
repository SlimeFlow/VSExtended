package com.slimeflow.vsextended.math.curves;

public class EaseOutCurve implements ICurve {

    private int factor = 0;

    public EaseOutCurve(int factor) {
        this.factor = factor;
    }



    @Override
    public double evaluate(double time) {
        return (Math.pow(time, this.factor + 1) / (Math.pow(time,2) + (1 - time)));
    }

    @Override
    public String getName() {
        return "Ease Out";
    }
}
