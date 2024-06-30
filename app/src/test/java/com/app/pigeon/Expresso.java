//package com.app.pigeon;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.Instrumentation;
//import android.content.Intent;
//
//import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.intent.Intents;
//import androidx.test.espresso.intent.rule.IntentsTestRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.rule.GrantPermissionRule;
//
//import com.app.pigeon.R;
//import com.app.pigeon.ui.ChatListActivity;
//import com.app.pigeon.ui.ContactActivity;
//import com.app.pigeon.ui.DeviceListActivity;
//import com.app.pigeon.ui.MainActivity;
//import com.app.pigeon.ui.SettingsActivity;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.Espresso.pressBack;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.intent.Intents.intending;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//
//@RunWith(AndroidJUnit4.class)
//public class MainActivityNavigationTest {
//
//    @Rule
//    public IntentsTestRule<MainActivity> activityRule = new IntentsTestRule<>(MainActivity.class);
//
//    @Rule
//    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
//
//    @Before
//    public void setUp() {
//        Intents.init();
//    }
//
//    @After
//    public void tearDown() {
//        Intents.release();
//    }
//
//    @Test
//    public void testNavigationThroughActivities() {
//
//        // Navigate to ContactActivity and back
//        onView(withId(R.id.contacts)).perform(click());
//        Intents.intended(hasComponent(ContactActivity.class.getName()));
//        pressBack();
//
//        // Navigate to SettingsActivity and back
//        onView(withId(R.id.action_settings)).perform(click());
//        Intents.intended(hasComponent(SettingsActivity.class.getName()));
//        pressBack();
//
//        // Navigate to ChatListActivity and back
//        onView(withId(R.id.chat_list)).perform(click());
//        Intents.intended(hasComponent(ChatListActivity.class.getName()));
//        pressBack();
//
//        // Test enabling Bluetooth
//        onView(withId(R.id.menu_enable_bluetooth)).perform(click());
//
//        // Mock the Bluetooth enable dialog
//        intending(hasComponent(Intent.ACTION_REQUEST_ENABLE)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
//        pressBack();
//
//        // Test searching for devices
//        onView(withId(R.id.menu_search_devices)).perform(click());
//
//    }
//
//    @Test
//    public void testPermissionDialog() {
//        // Mock the permission dialog
//        onView(withId(R.id.menu_search_devices)).perform(click());
//
//        // Grant the permission
//        onView(withText("Grant")).perform(click());
//
//        // Verify the permission dialog appears
//        onView(withText("Location permission is required.\nPlease grant permission.")).check(matches(isDisplayed()));
//
//        // Deny the permission
//        onView(withText("Deny")).perform(click());
//    }
//}
