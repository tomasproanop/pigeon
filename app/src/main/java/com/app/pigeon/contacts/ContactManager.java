package com.app.pigeon.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactManager {

    private final ContactDatabaseHelper dbHelper;

    public ContactManager(Context context) {
        dbHelper = new ContactDatabaseHelper(context);
    }

    public void saveContact(Contact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactDatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(ContactDatabaseHelper.COLUMN_ADDRESS, contact.getAddress());

        db.insert(ContactDatabaseHelper.TABLE_CONTACTS, null, values);
        db.close();
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(ContactDatabaseHelper.TABLE_CONTACTS,
                new String[]{ContactDatabaseHelper.COLUMN_ID, ContactDatabaseHelper.COLUMN_NAME, ContactDatabaseHelper.COLUMN_ADDRESS},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(ContactDatabaseHelper.COLUMN_ADDRESS));
                contacts.add(new ContactImpl(id, name, address));
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return contacts;
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ContactDatabaseHelper.TABLE_CONTACTS, ContactDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateContact(Contact contact) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactDatabaseHelper.COLUMN_NAME, contact.getName());
        values.put(ContactDatabaseHelper.COLUMN_ADDRESS, contact.getAddress());

        db.update(ContactDatabaseHelper.TABLE_CONTACTS, values, ContactDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }
}
