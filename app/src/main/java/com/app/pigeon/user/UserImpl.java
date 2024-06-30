package com.app.pigeon.user;

import com.app.pigeon.user.User;

/**
 * Represents a user in the application.
 */
public class UserImpl implements User {
    private String nickname;
    private String password;

    /**
     * Constructs a new User object with the specified nickname and password.
     *
     * @param nickname The nickname of the user.
     * @param password The password of the user.
     */
    public UserImpl(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    /**
     * Gets the nickname of the user.
     *
     * @return The nickname of the user.
     */
    public String getNickname() {

        return nickname;
    }

    /**
     * Sets the nickname of the user.
     *
     * @param nickname The new nickname of the user.
     */
    public void setNickname(String nickname) {

        this.nickname = nickname;
    }

    /**
     * Gets the password of the user.
     *
     * @return The password of the user.
     */
    public String getPassword() {

        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The new password of the user.
     */
    public void setPassword(String password) {

        this.password = password;
    }
}

