import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    //сохраняет всё в историю
    public void log(String message,String nameUser){
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd hh:mm:ss a zzz");

        String date = formatForDateNow.format(dateNow);
        String dateMessage = "Name: " + nameUser + " date : " + date;
        String textMessage = "message :" + message;

        try (FileWriter fileWriter = new FileWriter("history.txt",true);
             PrintWriter printWriter = new PrintWriter(fileWriter)){
            printWriter.printf("%s" + "%n",dateMessage);
            printWriter.printf("%s" + "%n",textMessage);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}