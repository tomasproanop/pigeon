package com.app.pigeon.ui;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.app.pigeon.R;
import com.app.pigeon.controller.BluetoothManager;
import com.app.pigeon.controller.BluetoothService;
import com.app.pigeon.controller.BluetoothUtilities;
import com.app.pigeon.model.ChatImpl;
import com.app.pigeon.controller.ChatStorage;
import com.app.pigeon.controller.ChatListDatabaseHelper;
import com.app.pigeon.model.ChatListImpl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.BroadcastReceiver;

/**
 * This is the chat activity, where messages can be sent, erased and received
 */
public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_VOICE_NOTE = 3;

    private BluetoothService bluetoothService;
    private boolean isBound = false;
    private BluetoothUtilities bluetoothUtilities;
    private BluetoothAdapter bluetoothAdapter;

    private ListView messageListView;
    private ChatMessageAdapter adapter;


    private List<String> messageList = new ArrayList<>();

    private ChatImpl chatImpl;
    private EditText messageInput;
    private Button sendButton;
    private ImageButton sendPictureButton;
    private ImageButton sendVoiceNoteButton;
    //private ListView chatListView;

    private String currentPhotoPath;
    private ChatStorage chatStorage;
    private ChatListDatabaseHelper chatListDatabaseHelper;
    private String contactAddress;
    private String contactName;

    private StringBuffer myStringBuffer;
    public static Context context;

    private BluetoothUtilities.ConnectedThread connectedThread;


    // Constants for handler messages
    public static final int MESSAGE_STATE_CHANGED = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_READ = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    // storage

    private ChatListImpl chatListImpl;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            //
            bluetoothUtilities = BluetoothManager.getInstance(context, handler);
            //
            bluetoothUtilities = bluetoothService.getBluetoothUtilities();
            isBound = true;

            Log.d(TAG, "Service connected and BluetoothUtilities ready");

            connectToPairedDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            Log.d(TAG, "Service disconnected");
        }
    };


    // Define a Handler to handle messages from Bluetooth
    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGED:
                    Log.d(TAG, "MESSAGE_STATE_CHANGED: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothUtilities.STATE_NONE:
                        case BluetoothUtilities.STATE_LISTEN:
                            setState("Not connected");
                            break;
                        case BluetoothUtilities.STATE_CONNECTING:
                            setState("Connecting...");
                            break;
                        case BluetoothUtilities.STATE_CONNECTED:
                            setState("Connected: " + contactName);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, "MESSAGE_WRITE CA: " + writeMessage);
                    chatStorage.saveMessage(contactAddress, "Me: " + writeMessage, String.valueOf(System.currentTimeMillis()));
                    addMessage("Me 4: " + writeMessage);
                    loadMessages();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d(TAG, "MESSAGE_READ (CA): " + readMessage);
                    onMessageReceived(contactName + ": " + readMessage); // Call to handle received message
                    chatStorage.saveMessage(contactAddress, contactName + ": " + readMessage, String.valueOf(System.currentTimeMillis()));
                    //---
                    addMessage(contactName + ": " + readMessage);
                    //---
                    adapter.add(contactName + ": " + readMessage);
                    loadMessages();
                    //return true;

                    // Update UI with received message
                    runOnUiThread(() -> {
                        // runs on the main thread
                        messageList.add("Received: " + readMessage);
                        adapter.notifyDataSetChanged();
                    });
                    break;
                case MESSAGE_DEVICE_NAME:
                    contactName = msg.getData().getString(DEVICE_NAME);
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(ChatActivity.this, msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BroadcastReceiver onReceive triggered");
            if ("BluetoothMessageReceived".equals(intent.getAction())) {
                String message = intent.getStringExtra("message");
                Log.d(TAG, "BroadcastReceiver received message: " + message);
                //addMessage("Received: " + message);
                if (message != null) {
                    onMessageReceived(message);
                } else {
                    Log.d("ChatActivity", "No message found in intent");
                }
            } else {
                Log.d("ChatActivity", "Unexpected intent action: " + intent.getAction());
            }
        }
    };


    private void setState(CharSequence subTitle) {
        getSupportActionBar().setSubtitle(subTitle);
    }


    @SuppressLint({"MissingPermission", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListDatabaseHelper = new ChatListDatabaseHelper(this);
        context = this;

        //init();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        checkBluetoothPermissions();

         //Bind to BluetoothService
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Initialize UI elements
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        sendPictureButton = findViewById(R.id.send_picture_button);
        sendVoiceNoteButton = findViewById(R.id.send_voice_note_button);

        // Retrieve contact information from the intent
        contactAddress = getIntent().getStringExtra("contact_address");
        contactName = getIntent().getStringExtra("contact_name");
        setTitle(contactName);

        // Initialize BluetoothUtilities and other components
        //bluetoothUtilities = BluetoothManager.getInstance(this, handler);
        bluetoothUtilities = new BluetoothUtilities(context, handler);
        //chatImpl = new ChatImpl(this, bluetoothUtilities);
        chatStorage = new ChatStorage(ChatActivity.this);

        // Initialize ChatListImpl
        chatListImpl = new ChatListImpl(ChatActivity.this);


        // Start the Bluetooth service
        bluetoothUtilities.start();

        //messages
        //messageList = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, messageList);
        messageListView = findViewById(R.id.chat_list_2);
        messageListView.setAdapter(adapter);


        //addMessage("This is just an initial message :)");

        // Load previously saved messages
        loadMessages();

        // Register BroadcastReceiver to listen for messages
        IntentFilter filter = new IntentFilter("BluetoothMessageReceived");
        registerReceiver(bluetoothReceiver, filter);

        // Set up the send button click listener
        sendButton.setOnClickListener(v -> sendMessage());

        // Set up the send picture button click listener
        sendPictureButton.setOnClickListener(v -> {
            dispatchTakePictureIntent();
            sendMessage();
        });

        // go back button
        ImageButton goBackButton = findViewById(R.id.back_button);
        goBackButton.setOnClickListener(v -> {
            startActivity(new Intent(ChatActivity.this, ContactActivity.class));
        });

        // trash button to clear chat
        ImageButton menuButton = findViewById(R.id.delete_chat_button);
        menuButton.setOnClickListener(v -> clearChat());

        // Set up the send voice note button click listener
        sendVoiceNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchRecordVoiceNoteIntent();
            }
        });

        Log.d("ChatActivity", "ChatActivity created");

 }

//    private void init() {
//        EditText edCreateMessage = findViewById(R.id.message_input); //
//        Button btnSendMessage = findViewById(R.id.send_button); //

//        messageList = new ArrayList<>(); // list used by adapter
//        adapter = new ChatMessageAdapter(this, messageList);
//        ListView messageListView = findViewById(R.id.chat_list_2);
//        messageListView.setAdapter(adapter);

//        btnSendMessage.setOnClickListener(view -> {
//            String message = edCreateMessage.getText().toString();
//            if (!message.isEmpty()) {
//                edCreateMessage.setText("");
//                bluetoothUtilities.write(message.getBytes());
//
//                // Add the message to the adapter to display in the ListView
//                addMessage("Me 1: " + message);
//                adapter.notifyDataSetChanged();
//            }
//        });
 //   }


    private void checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Log.d(TAG, "Bluetooth not enabled");
                    Toast.makeText(this, "Bluetooth not enabled, leaving", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                handleImageCapture();
                break;
            case REQUEST_VOICE_NOTE:
                handleVoiceNoteCapture(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImageCapture() {
        File f = new File(currentPhotoPath);
        Log.d(TAG, "Absolute Url of Image is " + Uri.fromFile(f));

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        // Send picture through Bluetooth
        bluetoothUtilities.write(contentUri.toString().getBytes());
    }

    private void handleVoiceNoteCapture(Intent data) {
        Uri audioUri = data.getData();
        Log.d(TAG, "Voice note recorded: " + audioUri);

        // Send voice note through Bluetooth
        bluetoothUtilities.write(audioUri.toString().getBytes());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error occurred while creating the File", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.app.pigeon.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchRecordVoiceNoteIntent() {
        Intent recordVoiceNoteIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(recordVoiceNoteIntent, REQUEST_VOICE_NOTE);
    }

    private void sendMessage() {
        String message = messageInput.getText().toString();
        BluetoothUtilities.ConnectedThread connectedThread = bluetoothUtilities.getConnectedThread();
        if (message.length() > 0 && connectedThread != null) {
            connectedThread.write(message.getBytes());
            // Add message to UI (from CA)
            addMessage("Me: " + message);
            messageInput.setText("");

            // Save message
            if (chatListImpl != null) {
                chatListImpl.saveMessage(contactName, "Me: " + message, String.valueOf(System.currentTimeMillis()));
            } else {
                Log.e(TAG, "chatListImpl is null, cannot save message");
            }
        } else {
            Toast.makeText(this, "Not connected to any device", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupChat() {
        Log.d(TAG, "setupChat()");

        chatStorage = new ChatStorage(this);
        chatImpl = new ChatImpl(this, bluetoothUtilities);

        loadMessages();

        // Initialize the buffer for outgoing messages
        myStringBuffer = new StringBuffer();

        //////// now
        messageList = new ArrayList<>();
        messageListView = findViewById(R.id.chat_list_2);
        messageListView.setAdapter(adapter);

        // Set up the send button click listener
        sendButton.setOnClickListener(v -> sendMessage());


    }

    private void loadMessages() {
        List<String> savedMessages = chatStorage.getMessages(contactAddress);
        if (savedMessages != null && !savedMessages.isEmpty()) {
            messageList.addAll(savedMessages);
            adapter.notifyDataSetChanged();
        }
    }
///////////////////////////////////////////////////////////////////////////////////

    private void addMessage(String message) {
        Log.d(TAG, "Adding message to list: " + message);
        runOnUiThread(() -> {
            Log.d(TAG, "Running on UI thread");
            messageList.add(message);
            Log.d(TAG, "Message added to messageList: " + message);
            Log.d(TAG, "Message list size: " + messageList.size());
            Log.d(TAG, "Notifying adapter of data change");
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Adapter notified of data change");
            messageListView.smoothScrollToPosition(messageList.size() - 1);
        });
    }

    // responsible updating UI with received messages
    // logs to keep track of the functionality
    public void onMessageReceived(String message) {
        Log.d("ChatActivity", "onMessageReceived called with message: " + message);
        runOnUiThread(() -> {
            Log.d("ChatActivity", "Running on UI thread to update UI with message: " + message);
            // actual message on UI
            messageList.add(contactName + ": " + message);
            Log.d("ChatActivity", "Message added to messageList: " + message);
            Log.d("ChatActivity", "Message list size: " + messageList.size());
            adapter.notifyDataSetChanged();
            Log.d("ChatActivity", "Adapter notified of data change");
            messageListView.smoothScrollToPosition(messageList.size() - 1);
            Log.d("ChatActivity", "Message list view scrolled to position: " + (messageList.size() - 1));

            // storage
            chatListImpl.saveMessage(contactName, "Friend: " + message, String.valueOf(System.currentTimeMillis()));

        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("BluetoothMessageReceived");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(bluetoothReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }
        Log.d("ChatActivity", "BroadcastReceiver registered");
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothReceiver);
        Log.d(TAG, "onPause called");
        Log.d("ChatActivity", "BroadcastReceiver unregistered");
    }

    private void connectToPairedDevice() {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(contactAddress);
        boolean secure = true;
        bluetoothUtilities.connect(device, secure);
    }

    private void clearChat() {
        messageList.clear();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(bluetoothReceiver);
//        if (isBound) {
//            unbindService(serviceConnection);
//            isBound = false;
//        }
    }

}
