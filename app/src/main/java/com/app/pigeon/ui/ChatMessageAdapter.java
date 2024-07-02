package com.app.pigeon.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.pigeon.R;

import java.util.List;

/**
 * This is the Chat Message Adapter
 */
public class ChatMessageAdapter extends ArrayAdapter<String> {

    public ChatMessageAdapter(Context context, List<String> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String message = getItem(position);

        // ViewHolder pattern for better performance
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.messageTextView = convertView.findViewById(R.id.message_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Check for null message and set text
        if (message != null) {
            viewHolder.messageTextView.setText(message);
        }

        return convertView;
    }

    // ViewHolder class for caching views
    private static class ViewHolder {
        TextView messageTextView;
    }
}
