import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class NewClient {
    static String login;
    static final String IP = "localhost";
    static final int PORT = 45001;

    static class ReadChat implements Runnable {
        Socket socket;
        ReadChat (Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
                try (InputStream in = socket.getInputStream()) {
                    while (true) {
                        byte[] mas = new byte[32 * 1024];
                        int bytes = in.read(mas);
                        //выводим это сообщение в окно new String(mas, 0, bytes)
                        System.out.println(new String(mas, 0, bytes - 1));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    static class WriteChat implements Runnable {
        Socket socket;

        WriteChat(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                OutputStream out = socket.getOutputStream();
                //при запуске отправляем логин
                out.write(login.getBytes());
                out.flush();
                while (true) {
                    //out.write(откуда считываем + "\n").getBytes());
                    Scanner scanner = new Scanner(System.in);
                    String msg = scanner.nextLine();
                    out.write((msg + "\n").getBytes());
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args) throws IOException, InterruptedException {
        //Первое сообщение которое мы отправляем на сервер - это наш ник!
        //те необходимо написать окошко с вводом ника и отправить его в качестве первого сообщения
        //логин необходимо поместить в поле login
        Scanner scanner = new Scanner(System.in);
        login = scanner.nextLine() + "\n";
        Socket mySocket = new Socket(IP, PORT);
        Thread thWrite = new Thread(new WriteChat(mySocket));
        thWrite.start();
        Thread thRead = new Thread(new ReadChat(mySocket));
        thRead.start();
    }
}
