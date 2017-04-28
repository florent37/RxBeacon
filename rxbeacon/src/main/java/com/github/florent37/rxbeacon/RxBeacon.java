package com.github.florent37.rxbeacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;

/**
 * Created by florentchampigny on 28/04/2017.
 */

public class RxBeacon {
    @Nullable
    BeaconConsumer beaconConsumer;
    private Context application;
    private Region region = null;
    private BeaconManager beaconManager;

    public RxBeacon(Context context) {
        this.application = context.getApplicationContext();
        this.beaconManager = BeaconManager.getInstanceForApplication(application);
    }

    public static RxBeacon with(Context context) {
        return new RxBeacon(context);
    }

    private Region getRegion() {
        if (this.region == null) {
            this.region = new Region("myMonitoringUniqueId", null, null, null);
        }
        return region;
    }

    public RxBeacon addBeaconParser(String parser) {
        beaconManager.getBeaconParsers()
                .add(new BeaconParser().
                        setBeaconLayout(parser));

        return this;
    }

    public RxBeacon region(Region region) {
        this.region = region;
        return this;
    }

    private Observable<Boolean> startup() {
        return Observable
                .create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull final ObservableEmitter<Boolean> objectObservableEmitter) throws Exception {
                        beaconConsumer = new BeaconConsumer() {
                            @Override
                            public void onBeaconServiceConnect() {
                                objectObservableEmitter.onNext(true);
                                objectObservableEmitter.onComplete();
                            }

                            @Override
                            public Context getApplicationContext() {
                                return application;
                            }

                            @Override
                            public void unbindService(ServiceConnection serviceConnection) {
                                application.unbindService(serviceConnection);
                            }

                            @Override
                            public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
                                return application.bindService(intent, serviceConnection, i);
                            }
                        };
                        beaconManager.bind(beaconConsumer);
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (beaconConsumer != null) {
                            beaconManager.unbind(beaconConsumer);
                        }
                    }
                });

    }

    public Observable<RxBeaconRange> beaconsInRegion() {
        return startup()
                .flatMap(new Function<Boolean, ObservableSource<RxBeaconRange>>() {
                    @Override
                    public ObservableSource<RxBeaconRange> apply(@NonNull Boolean aBoolean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<RxBeaconRange>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<RxBeaconRange> objectObservableEmitter) throws Exception {
                                beaconManager.addRangeNotifier(new RangeNotifier() {
                                    @Override
                                    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                                        objectObservableEmitter.onNext(new RxBeaconRange(collection, region));
                                    }
                                });
                                beaconManager.startRangingBeaconsInRegion(getRegion());
                            }
                        });
                    }
                });
    }

    public Observable<RxBeaconMonitor> monitor() {
        return startup()
                .flatMap(new Function<Boolean, ObservableSource<RxBeaconMonitor>>() {
                    @Override
                    public ObservableSource<RxBeaconMonitor> apply(@NonNull Boolean aBoolean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<RxBeaconMonitor>() {
                            @Override
                            public void subscribe(@NonNull final ObservableEmitter<RxBeaconMonitor> observableEmitter) throws Exception {
                                beaconManager.addMonitorNotifier(new MonitorNotifier(){
                                    @Override
                                    public void didEnterRegion(Region region) {
                                        observableEmitter.onNext(new RxBeaconMonitor(RxBeaconMonitor.State.ENTER, region));
                                    }

                                    @Override
                                    public void didExitRegion(Region region) {
                                        observableEmitter.onNext(new RxBeaconMonitor(RxBeaconMonitor.State.EXIT, region));
                                    }

                                    @Override
                                    public void didDetermineStateForRegion(int i, Region region) {
                                        observableEmitter.onNext(new RxBeaconMonitor(null, region));
                                    }
                                });
                                beaconManager.startMonitoringBeaconsInRegion(getRegion());
                            }
                        });
                    }
                });
    }
}
