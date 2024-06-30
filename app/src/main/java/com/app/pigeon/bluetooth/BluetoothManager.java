package com.app.pigeon.bluetooth;

import android.content.Context;
import android.os.Handler;


public class BluetoothManager {
    public static BluetoothUtilities bluetoothUtilities;

    public static BluetoothUtilities getInstance(Context context, Handler handler) {
        if (bluetoothUtilities == null) {
            bluetoothUtilities = new BluetoothUtilities(context, handler);
        }
        return bluetoothUtilities;
    }
}
