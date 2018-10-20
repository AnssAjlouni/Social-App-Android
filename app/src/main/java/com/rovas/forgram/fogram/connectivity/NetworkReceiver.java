package com.rovas.forgram.fogram.connectivity;

import android.util.Log;

/**
 * Created by Mohamed El Sayed
 */

public class NetworkReceiver extends AbstractNetworkReceiver {
    private static final String TAG = NetworkReceiver.class.getName();

    public NetworkReceiver() {
        // required to avoid
        // Unable to instantiate receiver:
        // java.lang.InstantiationException: <NetworkReceiver> has no zero argument constructor
    }

    @Override
    public void noNetwork() {
        Log.d(TAG, "noNetwork");
    }

    @Override
    public void mobileNetwork() {
        Log.d(TAG, "mobileNetwork");
    }

    @Override
    public void WIFINetwork() {
        Log.d(TAG, "WIFINetwork");
    }
}