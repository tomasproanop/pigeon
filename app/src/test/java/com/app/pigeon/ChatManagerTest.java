//package com.app.pigeon;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;
//
//import android.bluetooth.BluetoothDevice;
//import android.content.Context;
//import android.os.Handler;
//
//import com.app.pigeon.bluetooth.BluetoothUtilities;
//import com.app.pigeon.chat.ChatManager;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//@RunWith(MockitoJUnitRunner.class)
//public class ChatManagerTest {
//
//    private ChatManager chatManager;
//
//    @Mock
//    Context mockContext;
//
//    @Mock
//    Handler mockHandler;
//
//    @Mock
//    BluetoothDevice mockDevice;
//
//    @Mock
//    BluetoothUtilities mockBluetoothUtilities;
//
//    @Before
//    public void setUp() {
//        chatManager = new ChatManager(mockContext, mockHandler);
//        chatManager.bluetoothUtilities = mockBluetoothUtilities;
//    }
//
//    @Test
//    public void testStartChat() {
//        chatManager.startChat();
//        verify(mockBluetoothUtilities).start();
//    }
//
//    @Test
//    public void testStopChat() {
//        chatManager.stopChat();
//        verify(mockBluetoothUtilities).stop();
//    }
//
//    @Test
//    public void testConnectDevice() {
//        chatManager.connectDevice(mockDevice);
//        verify(mockBluetoothUtilities).connect(mockDevice);
//    }
//
//    @Test
//    public void testSendMessage() {
//        byte[] message = "Test message".getBytes();
//        chatManager.sendMessage(message);
//        verify(mockBluetoothUtilities).write(message);
//    }
//
//    @Test
//    public void testGetChatState() {
//        when(mockBluetoothUtilities.getState()).thenReturn(BluetoothUtilities.STATE_CONNECTED);
//        int state = chatManager.getChatState();
//        assertEquals(BluetoothUtilities.STATE_CONNECTED, state);
//    }
//}
//
