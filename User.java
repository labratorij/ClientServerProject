package com.company;

import java.nio.ByteBuffer;

/**
 * Класс объекты которого хранят имя пользователя и его ByteBuffer
 */
public class User {
    private ByteBuffer byteBuffer;
    private String name;
    private int newConnect;

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
