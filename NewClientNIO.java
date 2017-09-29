package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * Клиент построенный на NIO
 */
public class NewClientNIO {
    static String login;
    static final String IP = "localhost";
    static final int PORT = 45001;
    private static ByteBuffer buffer = ByteBuffer.allocate(1024);
    static String msg;

    public static void main(String... args) throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(IP, PORT));
        //Отдельный поток для считывания с консоли
        // (если делать через графический интрфейс то нужно убрать)
        new Thread(() -> {
            //Первое сообщение которое мы отправляем на сервер - это наш ник!
            //те необходимо написать окошко с вводом ника и отправить его в качестве первого сообщения
            //логин необходимо поместить в поле login
            Scanner scannerMsg = new Scanner(System.in);
            while (true) {
                msg = scannerMsg.nextLine() + "\n";
                SelectionKey key = socketChannel.keyFor(selector);
                key.interestOps(OP_WRITE);
                selector.wakeup();
            }
        }).start();

        while (true) {
            selector.select();
            for (SelectionKey selectionKey : selector.selectedKeys()) {
                if (selectionKey.isConnectable()) {
                    socketChannel.finishConnect();
                    selectionKey.interestOps(OP_WRITE);
                } else if (selectionKey.isReadable()) {
                    buffer.clear();
                    int bytesRead = -1;
                    try {
                        bytesRead = socketChannel.read(buffer);
                    } catch (IOException e) {
                    }
                    if (bytesRead != -1 && bytesRead != 0) {
                        System.out.println(new String(buffer.array(), 0, bytesRead - 1));
                    }
                } else if (selectionKey.isWritable()) {
                    if (msg != null) {
                        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
                    }
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
            }
        }
    }
}
