package com.app.pigeon.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pigeon.R;
import com.app.pigeon.controller.DeviceListController;
import com.app.pigeon.model.Contact;
import com.app.pigeon.model.ContactImpl;
import com.app.pigeon.controller.ContactManager;
import com.app.pigeon.controller.BluetoothUtilities;

/**
 * In this activity it is possible to use the scan functionality,
 * which will enable to add pair devices as contacts to chat with
 */
public class DeviceListActivity extends AppCompatActivity implements AddContactDialog.AddContactDialogListener {

    private ListView listPairedDevices, listAvailableDevices;
    private ProgressBar progressScanDevices;

    private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices;
    private DeviceListController controller;
    private Context context;

    private BluetoothUtilities bluetoothUtilities;

    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_BT_SCAN = 2;

    private final int SELECT_DEVICE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context = this;

        init();
    }

        /**
     * Handles the result of an activity for selecting a device.
     * If the request code matches SELECT_DEVICE and the result is ok,
     * initiates a connection to the selected Bluetooth device.
     *
     * @param requestCode Request code passed to startActivityForResult().
     * @param resultCode  Result code returned by the child activity.
     * @param data        Intent, which can return result data to the caller.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data, boolean secure) {
        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK) {
            String address = data.getStringExtra("deviceAddress");
            bluetoothUtilities.connect(bluetoothAdapter.getRemoteDevice(address), secure);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        listPairedDevices = findViewById(R.id.list_paired_devices);
        listAvailableDevices = findViewById(R.id.list_available_devices);
        progressScanDevices = findViewById(R.id.progress_scan_devices);

        adapterPairedDevices = new ArrayAdapter<>(context, R.layout.device_list_item);
        adapterAvailableDevices = new ArrayAdapter<>(context, R.layout.device_list_item);

        listPairedDevices.setAdapter(adapterPairedDevices);
        listAvailableDevices.setAdapter(adapterAvailableDevices);

        controller = new DeviceListController(context, adapterPairedDevices, adapterAvailableDevices, progressScanDevices);

        listAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                Intent intent = new Intent();
                intent.putExtra("deviceAddress", address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                controller.scanDevices();

                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                String name = info.substring(0, info.length() - 18);

                Contact contact = new ContactImpl(name, address);
                new ContactManager(context).saveContact(contact);

                Log.d("Address", address);

                Intent intent = new Intent();
                intent.putExtra("deviceAddress", address);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        listPairedDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String info = adapterPairedDevices.getItem(position);
                String address = info.substring(info.length() - 17);

                AddContactDialog dialog = new AddContactDialog(address);
                dialog.show(getSupportFragmentManager(), "AddContactDialog");

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_scan_device) {
            controller.scanDevices();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
    }

    @Override
    public void onDialogPositiveClick(String contactName, String contactAddress) {
        controller.addPairedDeviceAsContact(contactName, contactAddress);

        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }
}
