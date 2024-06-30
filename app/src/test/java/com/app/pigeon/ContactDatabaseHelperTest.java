package com.app.pigeon;

import static org.junit.Assert.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import com.app.pigeon.contacts.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContactDatabaseHelperTest {

    private ContactDatabaseHelper dbHelper;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new ContactDatabaseHelper(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
    }

    @Test
    public void testDatabaseCreation() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    @Test
    public void testTableCreation() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("SELECT * FROM " + ContactDatabaseHelper.TABLE_CONTACTS);
        db.close();
    }
}
