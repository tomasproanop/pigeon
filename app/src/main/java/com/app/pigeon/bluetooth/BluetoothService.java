package com.app.pigeon.bluetooth;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BluetoothService extends Service {
    private BluetoothUtilities bluetoothUtilities;
    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothUtilities = new BluetoothUtilities(this, new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        Log.d("BluetoothService", "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String writeMessage = new String(writeBuf);
                        Log.d("BluetoothService", "MESSAGE_WRITE BS: " + writeMessage);
                        break;
                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        int bytesRead = msg.arg1;
                        if (bytesRead > 0) {
                            String readMessage = new String(readBuf, 0, bytesRead);
                            Log.d("BluetoothService", "MESSAGE_READ BS: " + readMessage);
                            handleMessageReceived(readBuf, bytesRead);
                        } else {
                            Log.e("BluetoothService", "Error: bytesRead is less than or equal to 0");
                        }
                        break;
                    case Constants.MESSAGE_DEVICE_NAME:
                        String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
                        Log.d("BluetoothService", "Connected to " + deviceName);
                        break;
                    case Constants.MESSAGE_TOAST:
                        String toastMessage = msg.getData().getString(Constants.TOAST);
                        Log.d("BluetoothService", "Toast: " + toastMessage);
                        break;
                }
                return false;
            }
        }));
    }

    public BluetoothUtilities getBluetoothUtilities() {
        return bluetoothUtilities;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothUtilities != null) {
            bluetoothUtilities.stop();
        }
    }

    private void handleMessageReceived(byte[] buffer, int bytes) {
        String message = new String(buffer, 0, bytes);
        Log.d("BluetoothService", "Message received BS: " + message);

        // Send the message to the UI or other components
        Intent intent = new Intent("BluetoothMessageReceived");
        intent.putExtra("message", message);
        Log.d("BluetoothService", "Sending broadcast with message: " + message);
        sendBroadcast(intent);
    }
}
