package com.app.pigeon.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.pigeon.R;
import com.app.pigeon.model.ChatList;
import com.app.pigeon.model.ChatListImpl;
import com.app.pigeon.model.ChatElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the chat list activity. It is not completely implemented yet.
 */
public class ChatListActivity extends AppCompatActivity {

    private ChatList chatList;
    private ChatListAdapter chatListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        chatList = new ChatListImpl(this);
        ListView chatListView = findViewById(R.id.chat_list);
        chatListAdapter = new ChatListAdapter(this, new ArrayList<>());
        chatListView.setAdapter(chatListAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChats();
                swipeRefreshLayout.setRefreshing(false);  // Stop the refreshing animation
            }
        });

        loadChats();

        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final ChatElement chatElement = (ChatElement) parent.getItemAtPosition(position);
                new AlertDialog.Builder(ChatListActivity.this)
                        .setTitle("Delete Chat")
                        .setMessage("Are you sure you want to delete this chat?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            chatList.deleteChat(Integer.parseInt(chatElement.getId()));
                            loadChats();
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }

    private void loadChats() {
        chatListAdapter.clear();

        // Adding a dummy item to verify UI functionality
        ChatElement dummyChat = new ChatElement("1", "Dummy Contact", "This is a preview message", "Timestamp");
        chatListAdapter.add(dummyChat);

        // Adding a dummy item to verify UI functionality
        ChatElement dummyChat2 = new ChatElement("2", "Dummy Contact", "This is a preview message", "Timestamp");
        chatListAdapter.add(dummyChat2);

        // Adding a dummy item to verify UI functionality
        ChatElement dummyChat3 = new ChatElement("3", "Dummy Contact", "This is a preview message", "Timestamp");
        chatListAdapter.add(dummyChat3);

        // Original code to load chats from database
        List<ChatElement> chats = chatList.getAllChats();
        chatListAdapter.addAll(chats);
    }
}
