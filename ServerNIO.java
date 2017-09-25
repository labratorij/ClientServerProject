package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerNIO {
    private static final Map<SocketChannel, User> sockets = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(45001));
        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start");

        while (true) {
            selector.select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        System.out.println("Connect " + socketChannel.getRemoteAddress());
                        User user = new User(ByteBuffer.allocate(1024));
                        sockets.put(socketChannel, user);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        user.setNewConnect(1); //говорит о том что первая строка - это ник
                    } else if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = sockets.get(socketChannel).getByteBuffer();
                            int bytesRead = socketChannel.read(buffer);
                            User user = sockets.get(socketChannel);
                            if (user.getNewConnect() == 0) {

                                if (bytesRead > 0 && buffer.get(buffer.position() - 1) == '\n') {
                                    System.out.println("read: " + new String(buffer.array(), 0, bytesRead - 1));
                                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                                }

                                if (bytesRead == -1) {
                                    System.out.println("Connect close " + socketChannel.getRemoteAddress());
                                    sockets.remove(socketChannel);
                                    socketChannel.close();
                                }

                            } else {
                                user.setName(new String(buffer.array(), 0, bytesRead - 1));
                                System.out.println("nick: " + user.getName());
                                socketChannel.register(selector, SelectionKey.OP_READ);
                                buffer.clear();
                                user.setNewConnect(0);
                            }
                    } else if (key.isWritable()) {
                        SocketChannel thisSocketChannel = (SocketChannel)key.channel();
                        ByteBuffer buffer = sockets.get(thisSocketChannel).getByteBuffer();
                        buffer.flip();
                        String clientMessage = new String(buffer.array(), buffer.position(), buffer.limit());
                        for (Map.Entry<SocketChannel, User> socketChannel2 : sockets.entrySet()) {
                            buffer.clear();
                            buffer.put(ByteBuffer.wrap((sockets.get(thisSocketChannel).getName() + ": " + clientMessage).getBytes()));
                            buffer.flip();
                            SocketChannel socketChannel = socketChannel2.getKey();
                            int bytesWritten = socketChannel.write(buffer);
                            System.out.println("write " + socketChannel.getRemoteAddress() + " " + new String(buffer.array(), 0, bytesWritten - 1));
                        }
                        if (!buffer.hasRemaining()) {
                            buffer.compact();
                            thisSocketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    }
                }
            }
            selector.selectedKeys().clear();
        }
    }
}
