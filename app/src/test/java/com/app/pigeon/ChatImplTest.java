package com.app.pigeon;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.graphics.Bitmap;

import com.app.pigeon.controller.BluetoothUtilities;
import com.app.pigeon.model.ChatImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// passes: text and picture. audio not tested yet.
public class ChatImplTest {

    @Mock
    Context mockContext;
    @Mock
    BluetoothUtilities mockBluetoothUtilities;
    @Mock
    BluetoothUtilities.ConnectedThread mockConnectedThread;

    private ChatImpl chatImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chatImpl = new ChatImpl(mockContext, mockBluetoothUtilities);
        chatImpl.connectedThread = mockConnectedThread;
    }

    @Test
    public void testSendMessage() {
        chatImpl.sendMessage("Hello World");
        verify(mockConnectedThread).write(any(byte[].class));
    }

    @Test
    public void testSendPicture() {
        Bitmap mockBitmap = mock(Bitmap.class);
        chatImpl.sendPicture(mockBitmap);
        verify(mockConnectedThread).write(any(byte[].class));
    }

//    @Test
//    public void testSendVoiceNote() {
//        chatImpl.sendVoiceNote("");
//        verify(mockConnectedThread).write(any(byte[].class));
//    }
}

