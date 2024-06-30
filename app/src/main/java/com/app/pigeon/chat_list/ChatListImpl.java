package com.app.pigeon.chat_list;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ChatListImpl implements ChatList {

    private static final String TAG = "ChatListImpl";
    private SQLiteDatabase database;
    private List<ChatElement> chatList;
    private Context context;

    private ChatListDatabaseHelper dbHelper;

    public ChatListImpl(Context context) {
        this.context = context;
        this.chatList = new ArrayList<>();
        dbHelper = new ChatListDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void addChat(ChatElement chatElement) {
        chatList.add(chatElement);
        ContentValues values = new ContentValues();
        values.put("contact_name", chatElement.getContactName());
        values.put("message_preview", chatElement.getMessagePreview());
        values.put("timestamp", chatElement.getTimestamp());
        database.insert("chats", null, values);
    }

    @Override
    public List<ChatElement> getAllChats() {
        List<ChatElement> chatList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = database.query("chats", null, null, null, null, null, "timestamp DESC");
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex("contact_name"));
                @SuppressLint("Range") String messagePreview = cursor.getString(cursor.getColumnIndex("message_preview"));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

                Log.d("ChatListImpl", "Retrieved chat: " + contactName + " - " + messagePreview);

                ChatElement chatElement = new ChatElement(id, contactName, messagePreview, timestamp);
                chatList.add(chatElement);
            }
        } catch (Exception e) {
            Log.e("ChatListImpl", "Error retrieving chats", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return chatList;
    }

    @Override
    public void deleteChat(int chatId) {
        database.delete("chats", "id = ?", new String[]{String.valueOf(chatId)});
    }

    // New methods for handling messages
    public void saveMessage(String contactName, String message, String timestamp) {
        ContentValues values = new ContentValues();
        values.put("contact_name", contactName);
        values.put("message", message);
        values.put("timestamp", timestamp);
        database.insert("messages", null, values);
    }

    public List<String> getMessages(String contactName) {
        List<String> messageList = new ArrayList<>();
        Cursor cursor = database.query("messages", null, "contact_name = ?", new String[]{contactName}, null, null, "timestamp ASC");

        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messageList;
    }
}
