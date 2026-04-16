package com.snl.entitiy;

import java.util.UUID;

public class Player {
    private final String name;
    private final String email = "dumy";
    private final String id;
    private int position;

    public Player(String name) {
        this.name = name;
//        this.email = email;
        this.id = UUID.randomUUID().toString();
        this.position = 0;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
