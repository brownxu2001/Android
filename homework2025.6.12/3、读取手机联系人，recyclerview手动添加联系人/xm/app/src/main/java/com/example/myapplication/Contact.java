package com.example.myapplication;

public class Contact {
    private long id;
    private String name;
    private String phone;

    public Contact(long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}
