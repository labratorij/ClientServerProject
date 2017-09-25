package com.company;

import java.nio.ByteBuffer;

public class User {
    ByteBuffer byteBuffer;
    String name;
    int newConnect;

    User(ByteBuffer byteBuffer) {
        this.newConnect = 0;
        this.name = "unknown";
        this.byteBuffer = byteBuffer;
    }

    ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    String getName() {
        return this.name;
    }

    int getNewConnect() {
        return this.newConnect;
    }

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNewConnect(int newConnect) {
        this.newConnect = newConnect;
    }
}
