package com.app.pigeon.model;

import java.util.List;

public interface ChatList {
    void addChat(ChatElement chatElement);
    List<ChatElement> getAllChats();
    void deleteChat(int chatId);
    void saveMessage(String chatId, String message, String timestamp);
}