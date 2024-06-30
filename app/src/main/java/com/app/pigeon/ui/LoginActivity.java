package com.app.pigeon.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.pigeon.R;
import com.app.pigeon.user.UserDatabaseHelper;
import com.app.pigeon.user.UserImpl;

/**
 * Activity responsible for user login functionality.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editTextNickname, editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Initialize EditText and Button views
        editTextNickname = findViewById(R.id.editTextNickname);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Check if views are null
        if (editTextNickname == null || editTextPassword == null || buttonLogin == null) {
            // Handle null views
            Log.e(TAG, "One or more views are null.");
            return;
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editTextNickname.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Perform login validation
                if (isValidCredentials(nickname, password)) {
                    // Navigate to chat activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    // Finish the login activity
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid nickname or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Validates user credentials.
     * Login if user exists and create a new user if user does not exist
     *
     * @param nickname The entered nickname.
     * @param password The entered password.
     * @return True if the credentials are valid, false otherwise.
     */
    private boolean isValidCredentials(String nickname, String password) {
        try {
            // Create an instance of UserDatabaseHelper
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            // Retrieve user from the database based on the entered nickname
            UserImpl user = dbHelper.getUserByNickname(nickname);

            // Check if user exists
            if (user != null) {
                // User exists, check if password matches
                return user.getPassword().equals(password);
            } else {
                // If user does not exist, create a new user
                dbHelper.addUser(nickname, password);
                return true;
            }
        } catch (Exception e) {
            // Error, wenn z.B. falsches Password benutzt wird
            Log.e(TAG, "Error validating credentials: " + e.getMessage());
            return false;
        }
    }
}
