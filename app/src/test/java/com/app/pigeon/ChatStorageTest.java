package com.app.pigeon;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.pigeon.controller.ChatStorage;
import com.app.pigeon.controller.ChatListDatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

//not passing as not implemented yet
@RunWith(MockitoJUnitRunner.class)
public class ChatStorageTest {

    @Mock
    private SQLiteDatabase database;

    @Mock
    private ChatListDatabaseHelper dbHelper;

    @InjectMocks
    private ChatStorage chatStorage;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(dbHelper.getWritableDatabase()).thenReturn(database);
    }

    @Test
    public void testSaveMessage() {
        doNothing().when(database).beginTransaction();
        doNothing().when(database).endTransaction();
        ContentValues values = new ContentValues();
        values.put("contact_name", "testAddress");
        values.put("message", "testMessage");
        values.put("timestamp", "testTimestamp");

        chatStorage.saveMessage("testAddress", "testMessage", "testTimestamp");

        verify(database).insert("messages", null, values);
        verify(database).setTransactionSuccessful();
    }

    @Test
    public void testGetMessages() {
        Cursor cursor = mock(Cursor.class);
        when(database.query(
                eq("messages"),
                any(String[].class),
                eq("contact_name = ?"),
                any(String[].class),
                isNull(),
                isNull(),
                eq("timestamp ASC")
        )).thenReturn(cursor);

        when(cursor.moveToFirst()).thenReturn(true);
        when(cursor.getString(cursor.getColumnIndexOrThrow("message"))).thenReturn("testMessage");
        when(cursor.moveToNext()).thenReturn(false);

        List<String> messages = chatStorage.getMessages("testContact");

        assertEquals(1, messages.size());
        assertEquals("testMessage", messages.get(0));

        cursor.close();
    }

    @Test
    public void testDeleteMessage() {
        chatStorage.deleteMessage("testAddress", "testMessage");
        verify(database).delete("messages", "contact_name = ? AND message = ?", new String[]{"testAddress", "testMessage"});
    }
}
