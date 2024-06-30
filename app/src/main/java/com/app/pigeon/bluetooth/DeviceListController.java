package com.app.pigeon.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.pigeon.contacts.Contact;
import com.app.pigeon.contacts.ContactImpl;
import com.app.pigeon.contacts.ContactManager;

import java.util.Set;

public class DeviceListController {

    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private final ArrayAdapter<String> adapterPairedDevices;
    private final ArrayAdapter<String> adapterAvailableDevices;
    private final ProgressBar progressScanDevices;

    private final ContactManager contactManager;

    private final BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    adapterAvailableDevices.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressScanDevices.setVisibility(ProgressBar.GONE);
                if (adapterAvailableDevices.getCount() == 0) {
                    Toast.makeText(context, "No new devices found.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Click on the device to start the chat.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public DeviceListController(Context context, ArrayAdapter<String> adapterPairedDevices, ArrayAdapter<String> adapterAvailableDevices, ProgressBar progressScanDevices) {
        this.context = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.adapterPairedDevices = adapterPairedDevices;
        this.adapterAvailableDevices = adapterAvailableDevices;
        this.progressScanDevices = progressScanDevices;
        this.contactManager = new ContactManager(context);
        initialize();
    }

    @SuppressLint("MissingPermission")
    public void initialize() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                adapterPairedDevices.add(device.getName() + "\n" + device.getAddress());
            }
        }

        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(bluetoothDeviceListener, foundFilter);
        IntentFilter discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(bluetoothDeviceListener, discoveryFinishedFilter);
    }

    @SuppressLint("MissingPermission")
    private void startDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startDiscovery();

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    @SuppressLint("MissingPermission") String deviceName = device.getName();
                    String deviceAddress = device.getAddress(); // MAC address

                    if (deviceName != null && !deviceName.isEmpty()) {
                        adapterAvailableDevices.add(deviceName + "\n" + deviceAddress);
                    } else {
                        adapterAvailableDevices.add(deviceAddress);
                    }
                    adapterAvailableDevices.notifyDataSetChanged();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    progressScanDevices.setVisibility(View.GONE);
                }
            }
        };

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filter);
    }

    @SuppressLint("MissingPermission")
    public void scanDevices() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            return;
        }

        progressScanDevices.setVisibility(ProgressBar.VISIBLE);
        adapterAvailableDevices.clear();
        Toast.makeText(context, "Scan started", Toast.LENGTH_SHORT).show();

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }


    public void onDestroy() {
        context.unregisterReceiver(bluetoothDeviceListener);
    }

    public void addPairedDeviceAsContact(String name, String address) {
        Contact newContact = new ContactImpl(name, address);
        contactManager.saveContact(newContact);
    }
}
