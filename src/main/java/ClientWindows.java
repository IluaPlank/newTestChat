import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ClientWindows extends JFrame implements ActionListener , TCPConnectionListn{

    private static final String IP = "localhost";
    private static final int WIDTH= 600;
    private static final int HEIGHT= 400;


    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindows();
            }
        });
    }
    private final JTextArea textArea = new JTextArea();
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    public ClientWindows(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        fieldInput.addActionListener(this);
        add(textArea, BorderLayout.CENTER);
        add(fieldInput,BorderLayout.SOUTH);
        try {
            connection = new TCPConnection(this,IP,serverSocketRead());
        } catch (IOException e) {
            printMes("Ошибка подключения : " + e);
        }
    }

    public int serverSocketRead() throws IOException {
        int socket = 0;
        var doc = new File("settings.txt");
        try {
            var obj = new BufferedReader(new FileReader(doc));
            String soc= obj.readLine();
            socket = Integer.parseInt(soc);

        } catch (FileNotFoundException e) {
            System.out.println("файл не найден :" + e);
        }
        return socket;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")){
            return;
        }
        else {
            fieldInput.setText(null);
        }
        connection.send(msg)    ;
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMes("Подключение установлено");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMes(value);
    }

    @Override
    public void onDisconnection(TCPConnection tcpConnection) {
        printMes("Подключение закрыто");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMes("Ошибка подключения : " + e);
    }
    private synchronized void printMes(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(msg + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });
    }
}
