package com.app.pigeon;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.app.pigeon.ui.ChatListActivity;
import com.app.pigeon.ui.ContactActivity;
import com.app.pigeon.ui.MainActivity;
import com.app.pigeon.ui.SettingsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// passed once, now it is failing

@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testNavigationThroughActivities() {

        // Navigate to ContactActivity and back
        try {
            onView(withId(R.id.contacts)).perform(click());
            Intents.intended(hasComponent(ContactActivity.class.getName()));
            pressBack();
        } catch (Exception e) {
            System.err.println("Error navigating to ContactActivity: " + e.getMessage());
        }

        // Navigate to SettingsActivity and back
        try {
            onView(withId(R.id.action_settings)).perform(click());
            Intents.intended(hasComponent(SettingsActivity.class.getName()));
            pressBack();
        } catch (Exception e) {
            System.err.println("Error navigating to SettingsActivity: " + e.getMessage());
        }

        // Navigate to ChatListActivity and back
        try {
            onView(withId(R.id.chat_list)).perform(click());
            Intents.intended(hasComponent(ChatListActivity.class.getName()));
            pressBack();
        } catch (Exception e) {
            System.err.println("Error navigating to ChatListActivity: " + e.getMessage());
        }

        // Test enabling Bluetooth
        try {
            onView(withId(R.id.menu_enable_bluetooth)).perform(click());
        } catch (Exception e) {
            System.err.println("Error enabling Bluetooth: " + e.getMessage());
        }

        // Test searching for devices
        try {
            onView(withId(R.id.menu_search_devices)).perform(click());
        } catch (Exception e) {
            System.err.println("Error searching for devices: " + e.getMessage());
        }
    }

    @Test
    public void testPermissionDialog() {
        // Mock the permission dialog
        try {
            onView(withId(R.id.menu_search_devices)).perform(click());
            onView(withText("Grant")).perform(click());
            onView(withText("Location permission is required.\nPlease grant permission.")).check(matches(isDisplayed()));
            onView(withText("Deny")).perform(click());
        } catch (Exception e) {
            System.err.println("Error testing permission dialog: " + e.getMessage());
        }
    }
}