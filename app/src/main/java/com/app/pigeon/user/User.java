package com.app.pigeon.user;

/**
 *
 */
public interface User {

        // Abstract methods to be implemented by UserImpl class
        public abstract String getNickname();
        public abstract void setNickname(String nickname);
        public abstract String getPassword();
        public abstract void setPassword(String password);
}