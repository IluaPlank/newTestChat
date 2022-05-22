import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
    UsersReg reg = new UsersReg();
    private final TCPConnectionListn listen;
    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(TCPConnectionListn listen, String ipAdrs, int port) throws IOException {
        this(listen, new Socket(ipAdrs, port));
    }


    public TCPConnection(TCPConnectionListn listen, Socket socket) throws IOException {
        this.listen = listen;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listen.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()) {
                        var msg = in.readLine();
                        listen.onReceiveString(TCPConnection.this, msg);
                    }

                } catch (IOException e) {
                    listen.onException(TCPConnection.this, e);
                } finally {
                    listen.onDisconnection(TCPConnection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void send(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            listen.onException(TCPConnection.this, e);
            disconnected();
        }
    }

    public synchronized void disconnected() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException err) {
            listen.onException(TCPConnection.this, err);
        }
    }

    public String register() throws IOException {
        String name;
        while (true) {
            send("Вы зарегистрированы? да/нет");
            String regMes = in.readLine();
            if (regMes.equalsIgnoreCase("да")) {
                while (true) {
                    send("Введите логин");
                    String login = in.readLine();
                    send("Введите пароль");
                    String password = in.readLine();
                    if (reg.registerValet(login, password)) {
                        name = login;
                        break;
                    } else {
                        send("Неверный ввод");
                    }
                }
                break;
            } else if (regMes.equalsIgnoreCase("нет")) {
                while (true) {
                    send("Придумайте логин");
                    String login = in.readLine();
                    send("Придумайте пароль");
                    String password = in.readLine();
                    if (reg.registerUser(login, password)) {
                        name = login;
                        break;
                    } else {
                        send("Логин занят, повтори попытку");
                    }
                }
                break;
            } else send("неправильный ввод");
        }
        return name;
    }
}
