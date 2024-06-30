package com.app.pigeon.chat;

import android.graphics.Bitmap;

public interface Chat {
    void sendMessage(String message);
    void sendPicture(Bitmap bitmap);
    void sendVoiceNote(String voiceNotePath);
}
