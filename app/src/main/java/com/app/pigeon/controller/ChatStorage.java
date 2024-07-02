package com.app.pigeon.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the chat storage. This is not completely implemented yet.
 */
public class ChatStorage {
    private SQLiteDatabase database;
    private ChatListDatabaseHelper dbHelper;

    public ChatStorage(Context context) {
        dbHelper = new ChatListDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public synchronized void saveMessage(String contactAddress, String message, String timestamp) {
        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("contact_name", contactAddress);
            values.put("message", message);
            values.put("timestamp", timestamp);
            database.insert("messages", null, values);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }


    public List<String> getMessages(String contactName) {
        List<String> messages = new ArrayList<>();

        Cursor cursor = database.query(
                "messages",
                new String[]{"message"},
                "contact_name = ?",
                new String[]{contactName},
                null, null, "timestamp ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                messages.add(cursor.getString(cursor.getColumnIndexOrThrow("message")));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return messages;
    }

    public synchronized void deleteMessage(String contactAddress, String message) {
        database.delete("messages", "contact_name = ? AND message = ?", new String[]{contactAddress, message});
    }
}
