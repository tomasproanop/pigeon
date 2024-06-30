package com.app.pigeon.chat_list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChatListDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat_list.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Messages table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CONTACT_NAME = "contact_name";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONTACT_NAME + " TEXT, " +
                    COLUMN_MESSAGE + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER" +
                    ");";

    public ChatListDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("DatabaseHelper", "Creating database and tables");
        String CREATE_CHATS_TABLE = "CREATE TABLE chats ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "contact_name TEXT,"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_CHATS_TABLE);

        // create the messages table if not already done
        String CREATE_MESSAGES_TABLE = "CREATE TABLE messages ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "contact_name TEXT,"
                + "message TEXT,"
                + "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);

        Log.d("DatabaseHelper", "Database and tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS chats");
            db.execSQL("DROP TABLE IF EXISTS messages");
            onCreate(db);
        }
    }

}
