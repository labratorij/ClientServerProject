import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    static JTextArea textArea = new JTextArea();
    static JTextField textField = new JTextField();
    static JButton button = new JButton("OK");
    static String login;

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
                        textArea.append(new String(mas, 0, bytes));
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
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            out.write((textField.getText() + "\n").getBytes());
                            out.flush();
                            textField.setText("");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //GUI
    static class Window extends JFrame {
        Window() {
            super("Чат");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JPanel panel2 = new JPanel();
            panel2.setLayout(new GridLayout(1,2));
            textArea.setEnabled(false);
            textArea.setFont(new Font("PT Serif Caption",0,30));
            textArea.setLineWrap(true);
            JScrollPane pr = new JScrollPane(textArea);
            panel2.add(textField);
            panel2.add(button);
            panel.add(pr, BorderLayout.CENTER);
            panel.add(panel2, BorderLayout.SOUTH);
            setContentPane(panel);
            setSize(500,500);
        }
    }

    static class LoginWindow extends JFrame {
        LoginWindow() {
            super("Авторизация");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JTextField loginFiled = new JTextField();
            panel.add(loginFiled, BorderLayout.CENTER);
            JButton button = new JButton();
            button.setText("Choose this nick");
            panel.add(button, BorderLayout.SOUTH);
            setContentPane(panel);
            setSize(150,100);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    login = loginFiled.getText() + "\n";
                    JFrame window = new Window();
                    window.setVisible(true);
                    System.out.println("Chat start");
                    Socket socket = null;
                    try {
                        socket = new Socket("localhost", 45001);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Thread t1 = new Thread(new ReadChat(socket));
                    t1.start();
                    Thread t2 = new Thread(new WriteChat(socket));
                    t2.start();
                    setVisible(false);
                }
            });
        }
    }

    public static void main(String... args) throws IOException, InterruptedException {
        JFrame window = new LoginWindow();
        window.setVisible(true);
    }
}
