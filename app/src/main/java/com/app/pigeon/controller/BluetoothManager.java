package com.app.pigeon.controller;

import android.content.Context;
import android.os.Handler;


/**
 * This is just a Manager for Bluetooth Utilities
 */
public class BluetoothManager {
    public static BluetoothUtilities bluetoothUtilities;

    public static BluetoothUtilities getInstance(Context context, Handler handler) {
        if (bluetoothUtilities == null) {
            bluetoothUtilities = new BluetoothUtilities(context, handler);
        }
        return bluetoothUtilities;
    }
}
