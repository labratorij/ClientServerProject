package com.company;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Серверная часть чата (работает в одном потоке благодаря использованию NIO)
 * @version 1.0
 */
public class ServerNIO {
    private static final Map<SocketChannel, User> sockets = new ConcurrentHashMap<>();
    //создаем базу данных
    private static final sqlbdModul dataBase = new sqlbdModul();

    public static void main(String[] args) throws IOException {
        //Открываем ServerSocket канал для обработки подключения по адрессу localhost 45001
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(45001));
        serverChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Server start");

        //Обработка ключей селектора
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
                            int bytesRead = -1;
                            try {
                                bytesRead = socketChannel.read(buffer);
                            } catch (IOException e) {
                            }
                            User user = sockets.get(socketChannel);
                            if (user.getNewConnect() == 0) {
                                //Обработка сообщения в случае если оно не первое
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
                                //Обработка первого сообщения от клиента (по соглашению оно является его именем)
                                user.setName(new String(buffer.array(), 0, bytesRead - 1));
                                System.out.println("nick: " + user.getName());
                                socketChannel.register(selector, SelectionKey.OP_READ);
                                buffer.clear();
                                //Регистрация / Авторизация
                                if (user.getKey() == 'r') {
                                    dataBase.addPerson(user.getName(), user.getPassword());
                                    String lg = null;
                                    try {
                                        lg = dataBase.authorization(user.getName(), user.getPassword());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (lg == null) {
                                        System.out.println("cant find user");
                                    } else {
                                        user.setNewConnect(0);
                                    }
                                } else if (user.getKey() == 'a') {
                                    String lg = null;
                                    try {
                                        lg = dataBase.authorization(user.getName(), user.getPassword());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (lg == null) {
                                        System.out.println("cant find user");
                                        socketChannel.write(ByteBuffer.wrap("Error. Check your login / password \n".getBytes()));
                                    } else {
                                        //Вывод предыдущих 10 сообщений
                                        ArrayList<String> lastMsg = dataBase.getAllMessege();
                                        for (String msg : lastMsg) {
                                            socketChannel.write(ByteBuffer.wrap((msg + "\n").getBytes()));
                                        }
                                        user.setNewConnect(0);
                                    }
                                }
                            }
                    } else if (key.isWritable()) {
                        SocketChannel thisSocketChannel = (SocketChannel)key.channel();
                        ByteBuffer buffer = sockets.get(thisSocketChannel).getByteBuffer();
                        buffer.flip();
                        String clientMessage = new String(buffer.array(), buffer.position(), buffer.limit());
                        //Добавляем сообщение в БД
                        dataBase.addMassageBD(sockets.get(thisSocketChannel).getName(),clientMessage);
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
