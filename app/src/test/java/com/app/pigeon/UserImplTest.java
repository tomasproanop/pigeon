package com.app.pigeon;

import static org.junit.Assert.*;

import com.app.pigeon.model.UserImpl;

import org.junit.Before;
import org.junit.Test;

// tests passing
public class UserImplTest {

    private UserImpl user;

    @Before
    public void setUp() {
        user = new UserImpl("user1", "password123");
    }

    @Test
    public void testGetNickname() {
        assertEquals("user1", user.getNickname());
    }

    @Test
    public void testSetNickname() {
        user.setNickname("newUser");
        assertEquals("newUser", user.getNickname());
    }

    @Test
    public void testGetPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    public void testSetPassword() {
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword());
    }
}

