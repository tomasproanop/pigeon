//package com.app.pigeon.chat;
//
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.os.Handler;
//
//import com.app.pigeon.bluetooth.BluetoothUtilities;
//
//public class ChatManager {
//    public BluetoothUtilities bluetoothUtilities;
//
//    public ChatManager(Context context, Handler handler) {
//        bluetoothUtilities = new BluetoothUtilities(context, handler);
//    }
//
//    public void startChat() {
//        bluetoothUtilities.start();
//    }
//
//    public void stopChat() {
//        bluetoothUtilities.stop();
//    }
//
//    public void connectDevice(BluetoothDevice device) {
//        bluetoothUtilities.connect(device);
//    }
//
//    public void sendMessage(bytes[] message) {
//        bluetoothUtilities.write(message);
//    }
//
//    public int getChatState() {
//        return bluetoothUtilities.getState();
//    }
//}
