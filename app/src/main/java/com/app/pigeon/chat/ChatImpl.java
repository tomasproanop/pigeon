package com.app.pigeon.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.app.pigeon.bluetooth.BluetoothUtilities;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ChatImpl implements Chat {
    private Context context;
    private BluetoothUtilities bluetoothUtilities;

    private BluetoothUtilities.ConnectedThread connectedThread;

    public ChatImpl(Context context, BluetoothUtilities bluetoothUtilities) {
        this.context = context;
        this.bluetoothUtilities = bluetoothUtilities;
    }

    @Override
    public void sendMessage(String message) {
        Log.d("ChatActivity", "Sending message: " + message);
        connectedThread.write(message.getBytes());
    }
    @Override
    public void sendPicture(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        connectedThread.write(byteArray);
    }

    @Override
    public void sendVoiceNote(String voiceNotePath) {
        File file = new File(voiceNotePath);
        try {
            byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            connectedThread.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
