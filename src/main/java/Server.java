import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class Server implements TCPConnectionListn {
    Log log = new Log();
    private static int clients_count = 0;
    final int PORT = 8080;
    private final ArrayList<TCPConnection> clients = new ArrayList<>();
    private final Map<TCPConnection,String> nameUsers = new HashMap<>();

    public void server() {
        System.out.println("Сервер запущен!");
        portSave();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("Exception :" + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onConnectionReady(TCPConnection tcpConnection) {
        String  nameUser = null;
        try {
            nameUser = tcpConnection.register();
        } catch (IOException e) {
            System.out.println("Exception :" + e);
        }
        nameUsers.put(tcpConnection,nameUser);
        clients.add(tcpConnection);
        clients_count++;
        sendMessageToAllClients("Новый участник " + nameUser + " вошёл в чат!, для выхода наберите /exit ",nameUser);
        sendMessageToAllClients("Клиентов в чате = " + clients_count,nameUser);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        if (value.equals("/exit")){
            onDisconnection(tcpConnection);
        }
        String nameUser = nameUsers.get(tcpConnection);
        sendMessageToAllClients(value,nameUser);
    }

    @Override
    public synchronized void onDisconnection(TCPConnection tcpConnection) {
        String nameUser = nameUsers.get(tcpConnection);
        nameUsers.remove(tcpConnection);
        tcpConnection.send("Пока-пока!");
        clients.remove(tcpConnection);
        clients_count--;
        sendMessageToAllClients(nameUser + " покинул чат",nameUser);
        sendMessageToAllClients("Клиентов в чате = " + clients_count,nameUser);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("ошибка :" + e);
    }

    public void sendMessageToAllClients(String msg,String name) {
        log.log(msg,name);
        for (TCPConnection o : clients) {
            o.send(name +" : " + msg);
        }
    }

    private void portSave() {
        try (FileWriter fileWriter = new FileWriter("settings.txt");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(PORT);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
