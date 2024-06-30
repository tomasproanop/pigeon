package com.app.pigeon.contacts;

public class ContactImpl implements Contact {

    private int id;
    private String name;
    private String address;

    public ContactImpl(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public ContactImpl(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }
}
