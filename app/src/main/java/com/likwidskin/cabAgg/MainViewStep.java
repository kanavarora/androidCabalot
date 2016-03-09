package com.likwidskin.cabAgg;

/**
 * Created by kanavarora on 3/9/16.
 */
public enum MainViewStep {

    MainViewStepSetPickup(1),
    MainViewStepSetDest(2),
    MainViewStepSetOptimize(3);

    private int value;

    private MainViewStep(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
