package com.app.pigeon.user;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.pigeon.user.UserImpl;

/**
 * Helper class for managing user data in an SQLite database.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "User.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "users";
    private static final String COLUMN_NICKNAME = "nickname";
    private static final String COLUMN_PASSWORD = "password";

    /**
     * Constructor for the UserDatabaseHelper class.
     *
     * @param context The application context.
     */
    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NICKNAME + " TEXT PRIMARY KEY," +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableQuery);
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Adds a new user to the database.
     *
     * @param nickname The nickname of the user.
     * @param password The password of the user.
     */
    public void addUser(String nickname, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NICKNAME, nickname);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Retrieves a user from the database based on the nickname.
     *
     * @param nickname The nickname of the user to retrieve.
     * @return The User object if found, or null if not found.
     */
    public UserImpl getUserByNickname(String nickname) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NICKNAME, COLUMN_PASSWORD},
                COLUMN_NICKNAME + "=?", new String[]{nickname}, null, null, null);
        UserImpl user = null;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String userNickname = cursor.getString(cursor.getColumnIndex(COLUMN_NICKNAME));
            @SuppressLint("Range") String userPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            user = new UserImpl(userNickname, userPassword);
            cursor.close();
        }
        db.close();
        return user;
    }

    public void deleteUser(String nickname) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_NICKNAME + "=?", new String[]{nickname});
        db.close();
    }
}
