package com.github.florent37.rxbeacon;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Created by florentchampigny on 28/04/2017.
 */

public class RxBeaconRange {
    private final Collection<Beacon> beacons;
    private final Region region;

    public RxBeaconRange(Collection<Beacon> beacons, Region region) {
        this.beacons = beacons;
        this.region = region;
    }

    public Collection<Beacon> getBeacons() {
        return beacons;
    }

    public Region getRegion() {
        return region;
    }

    @Override
    public String toString() {
        return "RxBeaconRange{" +
                "beacons=" + beacons +
                ", region=" + region +
                '}';
    }
}
