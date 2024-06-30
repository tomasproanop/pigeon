//package com.app.pigeon.bluetooth;
//
//import android.annotation.SuppressLint;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//
//import com.app.pigeon.chat.ChatStorage;
//import com.app.pigeon.ui.ChatActivity;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.List;
//import java.util.UUID;
//
//public class BluetoothUtilities {
//    private final Context context;
//    private final Handler handler;
//    private final BluetoothAdapter bluetoothAdapter;
//    private ConnectThread connectThread;
//    private AcceptThread acceptThread;
//    private ConnectedThread connectedThread;
//
//    private final UUID APP_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
//    private final String APP_NAME = "Pigeon";
//
//    public static final int STATE_NONE = 0;
//    public static final int STATE_LISTEN = 1;
//    public static final int STATE_CONNECTING = 2;
//    public static final int STATE_CONNECTED = 3;
//
//    private int state;
//
//    public BluetoothUtilities(Context context, Handler handler) {
//        this.context = context;
//        this.handler = handler;
//        state = STATE_NONE;
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    }
//
//
//
//
//    public int getState() {
//        return state;
//    }
//
//    public synchronized void setState(int state) {
//        this.state = state;
//        handler.obtainMessage(ChatActivity.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
//    }
//
//    public synchronized void start() {
//        if (connectThread != null) {
//            connectThread.cancel();
//            connectThread = null;
//        }
//
//        if (acceptThread == null) {
//            acceptThread = new AcceptThread();
//            acceptThread.start();
//        }
//
//        if (connectedThread != null) {
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//
//        setState(STATE_LISTEN);
//    }
//
//    public synchronized void stop() {
//        if (connectThread != null) {
//            connectThread.cancel();
//            connectThread = null;
//        }
//        if (acceptThread != null) {
//            acceptThread.cancel();
//            acceptThread = null;
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
//    public void connect(BluetoothDevice device) {
//        if (state == STATE_CONNECTING) {
//            connectThread.cancel();
//            connectThread = null;
//        }
//
//        connectThread = new ConnectThread(device);
//        connectThread.start();
//
//        if (connectedThread != null) {
//            connectedThread.cancel();
//            connectedThread = null;
//        }
//
//        setState(STATE_CONNECTING);
//
//        // Send stored messages when connected
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                sendStoredMessages(device.getAddress());
//            }
//        }, 5000);
//    }
//
//    private void sendStoredMessages(String deviceAddress) {
//        ChatStorage chatStorage = new ChatStorage(context);
//        List<String> messages = chatStorage.getMessages(deviceAddress);
//        for (String message : messages) {
//            write(message.getBytes());
//            chatStorage.deleteMessage(deviceAddress, message);
//        }
//    }
//
//    public void write(byte[] buffer) {
//        ConnectedThread connThread;
//        synchronized (this) {
//            if (state != STATE_CONNECTED) {
//                Log.e("BluetoothUtilities", "Cannot write, not connected");
//                return;
//            }
//            connThread = connectedThread;
//        }
//        Log.d("BluetoothUtilities", "Writing message: " + new String(buffer));
//        connThread.write(buffer);
//    }
//
//    private class AcceptThread extends Thread {
//        private final BluetoothServerSocket serverSocket;
//
//        @SuppressLint("MissingPermission")
//        public AcceptThread() {
//            BluetoothServerSocket tmp = null;
//            try {
//                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID);
//            } catch (IOException e) {
//                Log.e("Accept->Constructor", e.toString());
//            }
//
//            serverSocket = tmp;
//        }
//
//        public void run() {
//            BluetoothSocket socket;
//            try {
//                socket = serverSocket.accept();
//            } catch (IOException e) {
//                Log.e("Accept->Run", e.toString());
//                try {
//                    serverSocket.close();
//                } catch (IOException e1) {
//                    Log.e("Accept->Close", e.toString());
//                }
//                return;
//            }
//
//            if (socket != null) {
//                synchronized (BluetoothUtilities.this) {
//                    switch (state) {
//                        case STATE_LISTEN:
//                        case STATE_CONNECTING:
//                            connected(socket, socket.getRemoteDevice());
//                            break;
//                        case STATE_NONE:
//                        case STATE_CONNECTED:
//                            try {
//                                socket.close();
//                            } catch (IOException e) {
//                                Log.e("Accept->CloseSocket", e.toString());
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//
//        public void cancel() {
//            try {
//                serverSocket.close();
//            } catch (IOException e) {
//                Log.e("Accept->CloseServer", e.toString());
//            }
//        }
//    }
//
//    private class ConnectThread extends Thread {
//        private final BluetoothSocket socket;
//        private final BluetoothDevice device;
//
//        @SuppressLint("MissingPermission")
//        public ConnectThread(BluetoothDevice device) {
//            this.device = device;
//            BluetoothSocket tmp = null;
//            try {
//                tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
//            } catch (IOException e) {
//                Log.e("Connect->Constructor", e.toString());
//            }
//            socket = tmp;
//        }
//
//        @SuppressLint("MissingPermission")
//        public void run() {
//            try {
//                socket.connect();
//            } catch (IOException e) {
//                Log.e("Connect->Run", e.toString());
//                try {
//                    socket.close();
//                } catch (IOException e1) {
//                    Log.e("Connect->CloseSocket", e.toString());
//                }
//                connectionFailed();
//                return;
//            }
//
//            synchronized (BluetoothUtilities.this) {
//                connectThread = null;
//            }
//
//            connected(socket, device);
//        }
//
////        public void run() {
////            bluetoothAdapter.cancelDiscovery();
////            try {
////                socket.connect();
////                synchronized (BluetoothUtilities.this) {
////                    connectThread = null;
////                }
////                connected(socket, device);
////            } catch (IOException e) {
////                connectionFailed();
////                try {
////                    socket.close();
////                } catch (IOException e2) {
////                    Log.e("ConnectThread", "Unable to close socket during connection failure", e2);
////                }
////                BluetoothUtilities.this.start();
////                return;
////            }
////        }
//
//        public void cancel() {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                Log.e("Connect->Cancel", e.toString());
//            }
//        }
//    }
//
//    public class ConnectedThread extends Thread {
//        private final BluetoothSocket socket;
//        private final InputStream inputStream;
//        private final OutputStream outputStream;
//
//        public ConnectedThread(BluetoothSocket socket) {
//            this.socket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) {
//                Log.e("Connected->Constructor", e.toString());
//            }
//
//            inputStream = tmpIn;
//            outputStream = tmpOut;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];
//            int bytes;
//
//            while (true) {
//                try {
//                    bytes = inputStream.read(buffer);
//                    handler.obtainMessage(ChatActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                } catch (IOException e) {
//                    connectionLost();
//                    break;
//                }
//            }
//        }
//
//        public void write(byte[] buffer) {
//            try {
//                outputStream.write(buffer);
//                handler.obtainMessage(ChatActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
//            } catch (IOException e) {
//                Log.e("Connected->Write", e.toString());
//            }
//        }
//
//        public void cancel() {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                Log.e("Connected->Cancel", e.toString());
//            }
//        }
//    }
//
//    public void connectionLost() {
//        Message message = handler.obtainMessage(ChatActivity.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(ChatActivity.TOAST, "Connection lost");
//        message.setData(bundle);
//        handler.sendMessage(message);
//
//        BluetoothUtilities.this.start();
//    }
//
//    public synchronized void connectionFailed() {
//        Message message = handler.obtainMessage(ChatActivity.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(ChatActivity.TOAST, "Can't connect to the device");
//        message.setData(bundle);
//        handler.sendMessage(message);
//
//        BluetoothUtilities.this.start();
//    }
//
//    @SuppressLint("MissingPermission")
//    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
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
//        connectedThread = new ConnectedThread(socket);
//        connectedThread.start();
//
//        Message message = handler.obtainMessage(ChatActivity.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(ChatActivity.DEVICE_NAME, device.getName());
//        message.setData(bundle);
//        handler.sendMessage(message);
//
//        setState(STATE_CONNECTED);
//    }
//}
