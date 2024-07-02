package com.app.pigeon;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.app.pigeon.ui.LoginActivity;
import com.app.pigeon.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

// passed once, not passing anymore

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityRule =
            new ActivityTestRule<>(LoginActivity.class);

    // fails
    @Test
    public void testLoginSuccess() {
        // Simulate user input for nickname and password
        onView(withId(R.id.editTextNickname)).perform(typeText("testUser"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("testPassword"), closeSoftKeyboard());

        // Perform button click
        onView(withId(R.id.buttonLogin)).perform(click());

        // Check if MainActivity is launched
        intended(hasComponent(MainActivity.class.getName()));
    }

    // pass
    @Test
    public void testLoginFailure() {
        // Simulate user input for nickname and password
        onView(withId(R.id.editTextNickname)).perform(typeText("testUser"), closeSoftKeyboard());
        onView(withId(R.id.editTextPassword)).perform(typeText("wrongPassword"), closeSoftKeyboard());

        // Perform button click
        onView(withId(R.id.buttonLogin)).perform(click());

        // Check if the Toast message is displayed
        onView(withText("Invalid nickname or password")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

