package com.likwidskin.cabAgg;

/**
 * Created by kanavarora on 3/16/16.
 */
public enum DestinationViewState {
    DestinationViewStateEmpty(1),
    DestinationViewStateFilled(2),
    DestinationViewStateLocked(3);

    private int value;

    private DestinationViewState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
