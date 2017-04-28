package com.github.florent37.rxbeacon.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.florent37.rxbeacon.RxBeacon;
import com.github.florent37.rxbeacon.RxBeaconRange;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxBeacon.with(this)
                //.addBeaconParser(RxBeaconParser.ESTIMOTE)
                .beaconsInRegion()
                .subscribe(new Consumer<RxBeaconRange>() {
                    @Override
                    public void accept(@NonNull RxBeaconRange rxBeaconRange) throws Exception {
                        Log.d("beaconsInRegion", rxBeaconRange.toString());
                    }
                });

        //RxBeacon.with(this)
        //        .addBeaconParser("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        //        .monitor()
        //        .subscribe(new Consumer<RxBeaconMonitor>() {
        //            @Override
        //            public void accept(@NonNull RxBeaconMonitor rxBeaconMonitor) throws Exception {
        //                Log.d("monitor", rxBeaconMonitor.toString());
        //            }
        //        });
    }
}
