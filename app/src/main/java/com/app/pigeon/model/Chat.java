package com.app.pigeon.model;

import android.graphics.Bitmap;

/**
 * Represents a chat
 */
public interface Chat {
    void sendMessage(String message);
    void sendPicture(Bitmap bitmap);
    void sendVoiceNote(String voiceNotePath);
}
