package com.app.pigeon.model;

/**
 * Representation of a contact
 */
public interface Contact {
    int getId();
    String getName();
    String getAddress();

    void setName(String string);
}

