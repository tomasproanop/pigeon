package com.app.pigeon.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.app.pigeon.R;
import com.app.pigeon.controller.UserDatabaseHelper;
import com.app.pigeon.model.UserImpl;

/**
 * This is the settings activity, where the username and password can be
 * changed. Also preferences regarding theme can be selected and saved.
 * Here is also a link to the GitHub repository.
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch notificationSwitch;
    private Switch darkModeSwitch;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationSwitch = findViewById(R.id.notification_switch);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);

        sharedPreferences = getSharedPreferences("UserSettings", Context.MODE_PRIVATE);

        // Load user settings
        loadUserSettings();

        findViewById(R.id.delete_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        findViewById(R.id.save_changes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void loadUserSettings() {
        boolean notificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", false);
        boolean darkModeEnabled = sharedPreferences.getBoolean("darkModeEnabled", false);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        notificationSwitch.setChecked(notificationsEnabled);
        darkModeSwitch.setChecked(darkModeEnabled);
        usernameEditText.setText(username);
        passwordEditText.setText(password);

        applyDarkMode(darkModeEnabled);
    }

    private void deleteAccount() {
        // Get the username from the EditText
        String username = usernameEditText.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an instance of UserDatabaseHelper
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);

        // Delete the user from the database
        dbHelper.deleteUser(username);

        // Show a confirmation message
        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();

        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        // Implement logic to sign out user
        Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();

        // Clear SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveChanges() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationsEnabled", notificationSwitch.isChecked());
        editor.putBoolean("darkModeEnabled", darkModeSwitch.isChecked());
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

        // Update database
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        UserImpl user = dbHelper.getUserByNickname(username);
        if (user != null) {
            dbHelper.deleteUser(username); // delete the old entry
            dbHelper.addUser(username, password); // add the new entry with the updated password
        }

        applyDarkMode(darkModeSwitch.isChecked());

        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    private void applyDarkMode(boolean darkModeEnabled) {
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
