package com.app.pigeon;

import static com.app.pigeon.ui.ChatActivity.context;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import com.app.pigeon.controller.BluetoothUtilities;
import com.app.pigeon.ui.ChatActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// not passing
@RunWith(MockitoJUnitRunner.class)
public class BluetoothUtilitiesTest {

    private BluetoothUtilities bluetoothUtilities;

    @Mock
    Context mockContext;

    @Mock
    Handler mockHandler;

    @Mock
    BluetoothDevice mockDevice;

    @Mock
    BluetoothSocket mockSocket;

    @Mock
    BluetoothAdapter mockBluetoothAdapter;

    @Mock
    InputStream mockInputStream;

    @Mock
    OutputStream mockOutputStream;

    @Before
    public void setUp() {
        bluetoothUtilities = new BluetoothUtilities(mockContext, mockHandler);
    }

    @Test
    public void testGetState() {
        assertEquals(BluetoothUtilities.STATE_NONE, bluetoothUtilities.getState());
    }

    @Test
    public void testSetState() {
        bluetoothUtilities.setState(BluetoothUtilities.STATE_CONNECTED);
        assertEquals(BluetoothUtilities.STATE_CONNECTED, bluetoothUtilities.getState());
        verify(mockHandler).obtainMessage(ChatActivity.MESSAGE_STATE_CHANGED, BluetoothUtilities.STATE_CONNECTED, -1);
    }

    @Test
    public void testStart() {
        bluetoothUtilities.start();
        assertEquals(BluetoothUtilities.STATE_LISTEN, bluetoothUtilities.getState());
    }

    @Test
    public void testStop() {
        bluetoothUtilities.stop();
        assertEquals(BluetoothUtilities.STATE_NONE, bluetoothUtilities.getState());
    }

    @Test
    public void testConnect() {
        boolean secure = true;
        bluetoothUtilities.connect(mockDevice, secure);
        assertEquals(BluetoothUtilities.STATE_CONNECTING, bluetoothUtilities.getState());
    }

    @Test
    public void testWrite() throws IOException {
        BluetoothUtilities.ConnectedThread connectedThread = bluetoothUtilities.new ConnectedThread(mockSocket, context);
        bluetoothUtilities.setState(BluetoothUtilities.STATE_CONNECTED);
        connectedThread.write("Test".getBytes());
        verify(mockOutputStream).write("Test".getBytes());
    }

    @Test
    public void testConnectionLost() {
        bluetoothUtilities.connectionLost();
        assertEquals(BluetoothUtilities.STATE_LISTEN, bluetoothUtilities.getState());
    }

    @Test
    public void testConnectionFailed() {
        bluetoothUtilities.connectionFailed();
        assertEquals(BluetoothUtilities.STATE_LISTEN, bluetoothUtilities.getState());
    }

    @Test
    public void testConnected() {
        String socketType = "secure";
        bluetoothUtilities.connected(mockSocket, mockDevice, socketType);
        assertEquals(BluetoothUtilities.STATE_CONNECTED, bluetoothUtilities.getState());
        verify(mockHandler).obtainMessage(ChatActivity.MESSAGE_DEVICE_NAME, -1, -1, mockDevice.getName());
    }
}
