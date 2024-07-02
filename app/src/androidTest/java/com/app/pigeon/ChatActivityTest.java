package com.app.pigeon;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.app.pigeon.ui.ChatActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

//not passing
@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {

    @Rule
    public ActivityScenarioRule<ChatActivity> activityRule =
            new ActivityScenarioRule<>(ChatActivity.class);

    @Test
    public void testSendMessageButtonVisibility() {
        // Check that the send button is visible
        Espresso.onView(ViewMatchers.withId(R.id.send_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSendMessageFunctionality() {
        // Type a message and click send
        String message = "Hello, World!";
        onView(withId(R.id.message_input)).perform(typeText(message));
        onView(withId(R.id.send_button)).perform(click());

        // Check that the message appears in the chat list
        onView(withId(R.id.chat_list_2)).check(matches(isDisplayed()));
        onView(withText("Me: " + message)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testSendPictureButton() {
//        // Check that the send picture button is visible and functional
//        onView(withId(R.id.send_picture_button))
//                .check(matches(isDisplayed()))
//                .perform(click());
//    }

    @Test
    public void testBackButtonFunctionality() {
        // Check that the back button takes the user to the ContactActivity
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.contacts)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteChatButton() {
        // Assuming there are some messages in the chat list
        onView(withId(R.id.delete_chat_button)).perform(click());
        onView(withId(R.id.chat_list_2)).check(matches(not(isDisplayed())));
    }

//    @Test
//    public void testSendVoiceNoteButton() {
//        // Check that the send voice note button is visible and functional
//        onView(withId(R.id.send_voice_note_button))
//                .check(matches(isDisplayed()))
//                .perform(click());
//    }

    @Test
    public void testMessageIsSent() {
        // Type a message and click send
        onView(withId(R.id.message_input)).perform(typeText("Test Message"));
        onView(withId(R.id.send_button)).perform(click());

        // Check that the message is displayed in the list
        onView(withText("Me: Test Message")).check(matches(isDisplayed()));
    }

    @Test
    public void testMessageListIsVisible() {
        // Check that the message list view is visible
        onView(withId(R.id.chat_list_2)).check(matches(isDisplayed()));
    }
}