package com.company;

import java.nio.ByteBuffer;

/**
 * Класс объекты которого хранят имя пользователя и его ByteBuffer
 */
public class User {
    private ByteBuffer byteBuffer;
    private String name;
    private String password;
    private char key;
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
        int after = 0;
        key = name.charAt(0);
        this.name = "";
        password = "";
        StringBuilder nameBr = new StringBuilder();
        StringBuilder passwordBr = new StringBuilder();

        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '$' && after == 0) {
                passwordBr.append(name.charAt(i));
            } else {
                after = 1;
            }
            if (name.charAt(i) != '$' && after == 1) {
                nameBr.append(name.charAt(i));
            }
        }
        this.name = nameBr.toString();
        password = passwordBr.toString();
    }

    public void setNewConnect(int newConnect) {
        this.newConnect = newConnect;
    }

    public String getPassword() {
        return password;
    }

    public char getKey() {
        return key;
    }
}
