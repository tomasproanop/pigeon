//package com.app.pigeon.bluetooth;
//
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//import com.app.pigeon.bluetooth.BluetoothUtilities.ConnectedThread;
//import com.app.pigeon.bluetooth.BluetoothUtilities.ConnectThread;
//
//public class BluetoothConnectionManager {
//    private static final String TAG = "BluetoothConnManager";
//
//    // Constants for message types
//    public static final int MESSAGE_STATE_CHANGE = 1;
//    public static final int MESSAGE_READ = 2;
//    public static final int MESSAGE_TOAST = 3;
//
//    // Constants for connection states
//    public static final int STATE_NONE = 0;
//    public static final int STATE_LISTEN = 1;
//    public static final int STATE_CONNECTING = 2;
//    public static final int STATE_CONNECTED = 3;
//
//    private final BluetoothAdapter bluetoothAdapter;
//    private final Handler handler = new Handler();
//    private ConnectThread connectThread;
//    private ConnectedThread connectedThread;
//
//    private BluetoothUtilities bluetoothUtilities;
//    private int state;
//    private Context context;
//
//    public BluetoothConnectionManager(Context context) {
//        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        this.bluetoothUtilities = new BluetoothUtilities(context, handler);
//        this.handler = new Handler(new BluetoothHandlerCallback());
//        this.state = STATE_NONE;
//        this.context = context;
//    }
//
//    private synchronized void setState(int state) {
//        this.state = state;
//        handler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
//    }
//
//    public synchronized int getState() {
//        return state;
//    }
//
//    public synchronized void connect(BluetoothDevice device, boolean secure) {
//        if (state == STATE_CONNECTING) {
//            if (connectThread != null) {
//                connectThread.cancel();
//                connectThread = null;
//            }
//        }
//
//        if (connectedThread != null) {
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//
//        connectThread = new ConnectThread(device, secure);
//        connectThread.start();
//        setState(STATE_CONNECTING);
//    }
//
//    @SuppressLint("MissingPermission")
//    private synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
//        if (connectThread != null) {
//            connectThread.cancel();
//            connectThread = null;
//        }
//
//        if (connectedThread != null) {
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//
//        connectedThread = ConnectedThread(socket, socketType);
//        connectedThread.start();
//
//        Message msg = handler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString("toast", "Connected to " + device.getName());
//        msg.setData(bundle);
//        handler.sendMessage(msg);
//
//        setState(STATE_CONNECTED);
//    }
//
//    private void connectionFailed() {
//        Message msg = handler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString("toast", "Unable to connect device");
//        msg.setData(bundle);
//        handler.sendMessage(msg);
//
//        setState(STATE_NONE);
//    }
//
//    private void connectionLost() {
//        Message msg = handler.obtainMessage(MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString("toast", "Device connection was lost");
//        msg.setData(bundle);
//        handler.sendMessage(msg);
//
//        setState(STATE_NONE);
//    }
//
//    public synchronized void stop() {
//        if (connectThread != null) {
//            connectThread.cancel();
//            connectThread = null;
//        }
//
//        if (connectedThread != null) {
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//
//        setState(STATE_NONE);
//    }
//
//    private class BluetoothHandlerCallback implements Handler.Callback {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case STATE_CONNECTED:
//                            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
//                            break;
//                        case STATE_CONNECTING:
//                            Toast.makeText(context, "Connecting", Toast.LENGTH_SHORT).show();
//                            break;
//                        case STATE_LISTEN:
//                        case STATE_NONE:
//                            Toast.makeText(context, "Not Connected", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                    break;
//                case MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Log.d(TAG, "Received: " + readMessage);
//                    break;
//                case MESSAGE_TOAST:
//                    Toast.makeText(context, msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
//                    break;
//            }
//            return true;
//        }
//    }
//}
