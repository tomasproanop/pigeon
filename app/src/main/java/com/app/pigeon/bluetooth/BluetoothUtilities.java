package com.app.pigeon.bluetooth;

import static com.app.pigeon.ui.ChatActivity.MESSAGE_READ;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.pigeon.R;
import com.app.pigeon.chat.Chat;
import com.app.pigeon.chat.MessageReceivedListener;
import com.app.pigeon.ui.ChatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothUtilities {

    //27.06
    //TextView messages = findViewById(R.id.message_text);
    //
    private static final String TAG = "BluetoothUtilities";

    // name sdp record
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";



    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FC");

    private final BluetoothAdapter bluetoothAdapter;
    private Context context;
    private final Handler handler;

    private String socketType;
    private AcceptThread secureAcceptThread;
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    private int newState;

    //private final MessageReceivedListener messageListener;


    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public BluetoothUtilities(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        this.handler = handler;
        state = STATE_NONE;
        newState = state;
        Log.d(TAG, "BluetoothUtilities initialized with handler: " + handler);
    }


    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + this.state + " -> " + state);
        this.state = state;
        handler.obtainMessage(ChatActivity.MESSAGE_STATE_CHANGED, state, -1).sendToTarget();
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket, context);
        connectedThread.start();

        // Get the Bluetooth device name
        BluetoothDevice device = socket.getRemoteDevice();
        @SuppressLint("MissingPermission") String deviceName = device.getName();

        // Send the device name back to the UI activity
        Message msg = handler.obtainMessage(ChatActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.DEVICE_NAME, deviceName);
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }
    public synchronized int getState() {
        return state;
    }

    public ConnectedThread getConnectedThread() {
        return connectedThread;
    }



    public synchronized void start() {
        Log.d(TAG, "start");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);

        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            secureAcceptThread.start();
        }

        if (insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread(false);
            insecureAcceptThread.start();
        }
    }

    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "Connect to: " + device);

        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    @SuppressLint("MissingPermission")
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        Log.d(TAG, "connected, socket type:" + socketType);

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }

        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }

        connectedThread = new ConnectedThread(socket, context);
        connectedThread.start();

        Message msg = handler.obtainMessage(ChatActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }

        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                Log.e(TAG, "not connected to any device");
                return;
            }
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    private void handleMessageReceived(byte[] buffer, int bytes) {
        String message = new String(buffer, 0, bytes);
        Log.d("BluetoothUtilities", "Message received BU handleMR: " + message);
        Message messageH = handler.obtainMessage();
        handler.obtainMessage(ChatActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
        //test2
        handler.dispatchMessage(messageH);


    }



    private void connectionFailed() {
        Message msg = handler.obtainMessage(ChatActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
        state = STATE_NONE;
        BluetoothUtilities.this.start();
    }

    private void connectionLost() {
        Message msg = handler.obtainMessage(ChatActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        handler.sendMessage(msg);
        state = STATE_NONE;

        BluetoothUtilities.this.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        private final String socketType;

        @SuppressLint("MissingPermission")
        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            try {
                if (secure) {
                    tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothSecure", MY_UUID_SECURE);
                } else {
                    tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothInsecure", MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "listen() failed", e);
            }
            serverSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + socketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + socketType);

            BluetoothSocket socket;

            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + socketType + "accept() failed", e);
                    break;
                }
                //test
                connectionLost();

                if (socket != null) {
                    synchronized (BluetoothUtilities.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice(), socketType);
                                manageConnectedSocket(socket);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + socketType);
        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + socketType + " cancel " + this);
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + socketType + "close() of server failed", e);
            }
        }
    }

    public class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private final String socketType;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device, boolean secure) {
            this.device = device;
            BluetoothSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";

            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "create() failed", e);
            }
            socket = tmp;
            state = STATE_CONNECTING;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + socketType);
            setName("ConnectThread" + socketType);

            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + socketType + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            synchronized (BluetoothUtilities.this) {
                connectThread = null;
            }

            connected(socket, device, socketType);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + socketType + " socket failed", e);
            }
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        ///private final String socketType;
        private final Context context;

        public ConnectedThread(BluetoothSocket socket, Context context) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            this.socket = socket;
            //this.socketType = socketType;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.context = context;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created/error obtaining InputStream/OutputStream", e);
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
            //setState(STATE_CONNECTED);
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        String message = new String(buffer, 0, bytes);
                        Log.d(TAG, "Received message in ConnectedThread: " + message);
                        Intent intent = new Intent("BluetoothMessageReceived");
                        intent.putExtra("message", message);
                        Log.d("BluetoothUtilities", "Sending broadcast with message: " + message);
                        context.sendBroadcast(intent);
                    } else {
                        Log.e(TAG, "Error: bytesRead is less than or equal to 0");
                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Input stream was disconnected", e);
                    break;
                }
            }

            // Close the socket
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                //test2
                handler.obtainMessage(ChatActivity.MESSAGE_WRITE, -1, -1, bytes)
                        .sendToTarget();
                Log.e(TAG, "write!");
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }


        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
