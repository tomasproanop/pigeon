package com.app.pigeon.chat_list;

public class ChatElement {
    private String id;
    private String contactName;
    private String messagePreview;
    private String timestamp;

    public ChatElement(String id, String contactName, String messagePreview, String timestamp) {
        this.id = id;
        this.contactName = contactName;
        this.messagePreview = messagePreview;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
