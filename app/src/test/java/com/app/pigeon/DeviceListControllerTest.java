package com.app.pigeon;
import com.app.pigeon.bluetooth.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class DeviceListControllerTest {

    private DeviceListController deviceListController;

    @Mock
    Context mockContext;

    @Mock
    ArrayAdapter<String> mockAdapterPairedDevices;

    @Mock
    ArrayAdapter<String> mockAdapterAvailableDevices;

    @Mock
    ProgressBar mockProgressScanDevices;

    @Mock
    BluetoothAdapter mockBluetoothAdapter;

    @Before
    public void setUp() {
        deviceListController = new DeviceListController(mockContext, mockAdapterPairedDevices, mockAdapterAvailableDevices, mockProgressScanDevices);
    }

    @Test
    public void testInitialize() {
        Set<BluetoothDevice> pairedDevices = new HashSet<>();
        BluetoothDevice mockDevice = mock(BluetoothDevice.class);
        when(mockDevice.getName()).thenReturn("Test Device");
        when(mockDevice.getAddress()).thenReturn("00:11:22:33:44:55");
        pairedDevices.add(mockDevice);
        when(mockBluetoothAdapter.getBondedDevices()).thenReturn(pairedDevices);

        deviceListController.initialize();

        verify(mockAdapterPairedDevices).add("Test Device\n00:11:22:33:44:55");
        verify(mockContext).registerReceiver(any(), any(IntentFilter.class));
    }

    @Test
    public void testScanDevices() {
        deviceListController.scanDevices();
        verify(mockProgressScanDevices).setVisibility(ProgressBar.VISIBLE);
        verify(mockAdapterAvailableDevices).clear();
        //verify(mockContext).makeText(mockContext, "Scan started", Toast.LENGTH_SHORT).show();
    }

    @Test
    public void testOnDestroy() {
        deviceListController.onDestroy();
        verify(mockContext).unregisterReceiver(any());
    }
}
