package com.app.pigeon;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import com.app.pigeon.controller.ContactDatabaseHelper;
import com.app.pigeon.controller.ContactManager;
import com.app.pigeon.model.Contact;
import com.app.pigeon.model.ContactImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

// tests not passing
public class ContactManagerTest {

    private ContactManager contactManager;
    private ContactDatabaseHelper dbHelper;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new ContactDatabaseHelper(context);
        contactManager = new ContactManager(context);
    }

    @After
    public void tearDown() {
        dbHelper.getWritableDatabase().delete(ContactDatabaseHelper.TABLE_CONTACTS, null, null);
    }

    @Test
    public void testSaveContact() {
        Contact contact = new ContactImpl("Test Name", "00:11:22:33:44:55");
        contactManager.saveContact(contact);

        List<Contact> contacts = contactManager.getAllContacts();
        assertEquals(1, contacts.size());
        assertEquals("Test Name", contacts.get(0).getName());
        assertEquals("00:11:22:33:44:55", contacts.get(0).getAddress());
    }

    @Test
    public void testGetAllContacts() {
        Contact contact1 = new ContactImpl("Test Name 1", "00:11:22:33:44:55");
        Contact contact2 = new ContactImpl("Test Name 2", "66:77:88:99:AA:BB");

        contactManager.saveContact(contact1);
        contactManager.saveContact(contact2);

        List<Contact> contacts = contactManager.getAllContacts();
        assertEquals(2, contacts.size());
    }

    @Test
    public void testDeleteContact() {
        Contact contact1 = new ContactImpl("Test Name 1", "00:11:22:33:44:55");
        Contact contact2 = new ContactImpl("Test Name 2", "66:77:88:99:AA:BB");

        contactManager.saveContact(contact1);
        contactManager.saveContact(contact2);

        List<Contact> contacts = contactManager.getAllContacts();
        assertEquals(2, contacts.size());

        contactManager.deleteContact(contacts.get(0).getId());

        contacts = contactManager.getAllContacts();
        assertEquals(1, contacts.size());
        assertEquals("Test Name 2", contacts.get(0).getName());
    }
}

