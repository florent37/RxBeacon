package com.github.florent37.rxbeacon;

import org.altbeacon.beacon.Region;

/**
 * Created by florentchampigny on 28/04/2017.
 */

public class RxBeaconMonitor {
    private Region region;
    private State state;
    public RxBeaconMonitor(State state, Region region) {
        this.region = region;
        this.state = state;
    }

    public Region getRegion() {
        return region;
    }

    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return "RxBeaconMonitor{" +
                "region=" + region +
                ", state=" + state +
                '}';
    }

    public enum State {
        ENTER,
        EXIT
    }
}
