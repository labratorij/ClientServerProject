package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Server {
    static ArrayList<Socket> sockets = new ArrayList<>();
    static StringBuilder ms = new StringBuilder();
    static int readyWrite = 0;

    static class User implements Runnable{
        String id;
        Socket socket;

        User (Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream()) {
                Send.addOutMas(out);
                byte bufLog[] = new byte[32 * 100];
                int readBytesLog = in.read(bufLog); //кол - во считанных байт
                this.id = new String(bufLog, 0, readBytesLog);
                while (true) {
                    byte buf[] = new byte[32 * 1024];
                    int readBytes = in.read(buf); //кол - во считанных байт
                    System.out.println("Read: " + new String(buf, 0, readBytes));
                    ms.append(id + ": " + new String(buf, 0, readBytes) + "\n");
                    readyWrite = 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class Send implements Runnable {
        static ArrayList<OutputStream> outMas = new ArrayList<>();

        static void addOutMas(OutputStream out) {
            outMas.add(out);
        }

        @Override
        public void run() {
            while (true) {
                if (readyWrite == 1) {
                    for (OutputStream out: outMas) {
                        try {
                            out.write(ms.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ms.delete(0,ms.length());
                    readyWrite = 0;
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1); //необходимо чтобы тред не закрылся
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String... args) {
        try (ServerSocket serverSocket = new ServerSocket(1500)){
            int id = 0;
            Thread trSend = new Thread(new Send());
            trSend.start();
            while (true) {
                System.out.println("Waiting connection");
                Socket socket = serverSocket.accept();
                Thread tr = new Thread(new User(socket));
                System.out.println("OK. User connect");
                sockets.add(socket);
                tr.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
