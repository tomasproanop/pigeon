package com.app.pigeon.chat_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.pigeon.R;
import com.app.pigeon.chat_list.ChatElement;

import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatElement> {

    private Context context;
    private List<ChatElement> chats;

    public ChatListAdapter(Context context, List<ChatElement> chats) {
        super(context, 0, chats);
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        }

        ChatElement chatElement = chats.get(position);

        TextView contactNameTextView = convertView.findViewById(R.id.contact_name);
        TextView messagePreviewTextView = convertView.findViewById(R.id.message_preview);
        TextView timestampTextView = convertView.findViewById(R.id.timestamp);

        contactNameTextView.setText(chatElement.getContactName());
        messagePreviewTextView.setText(chatElement.getMessagePreview());
        timestampTextView.setText(chatElement.getTimestamp());

        return convertView;
    }
}
