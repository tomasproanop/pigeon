package com.app.pigeon.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.pigeon.R;
import com.app.pigeon.model.Contact;
import com.app.pigeon.model.ContactImpl;
import com.app.pigeon.controller.ContactManager;

import java.util.List;

/**
 * In the contact activity, contacts can be managed
 */
public class ContactActivity extends AppCompatActivity implements AddContactDialog.AddContactDialogListener {

    private ContactManager contactManager;
    private ArrayAdapter<String> contactListAdapter;
    private ListView contactListView;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        contactManager = new ContactManager(this);
        contactListView = findViewById(R.id.contact_list_view);
        contactListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        contactListView.setAdapter(contactListAdapter);

        loadContacts();

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactManager.getAllContacts().get(position);
                Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
                intent.putExtra("contact_name", contact.getName());
                intent.putExtra("contact_address", contact.getAddress());
                startActivity(intent);
            }
        });

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactManager.getAllContacts().get(position);
                showEditDeleteDialog(contact);
                return true;
            }
        });
    }

    private void loadContacts() {
        contactListAdapter.clear();
        List<Contact> contacts = contactManager.getAllContacts();
        for (Contact contact : contacts) {
            contactListAdapter.add(contact.getName() + "\n" + contact.getAddress());
        }
    }

    private void showEditDeleteDialog(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Contact")
                .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showEditContactDialog(contact);
                                break;
                            case 1:
                                contactManager.deleteContact(contact.getId());
                                loadContacts();
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void showEditContactDialog(final Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Contact Name");

        final EditText input = new EditText(this);
        input.setText(contact.getName());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contact.setName(input.getText().toString());
                contactManager.updateContact(contact);
                loadContacts();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    @Override
    public void onDialogPositiveClick(String contactName, String contactAddress) {
        contactManager.saveContact(new ContactImpl(contactName, contactAddress));
        loadContacts();
    }
}
