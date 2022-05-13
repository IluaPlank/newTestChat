import java.util.concurrent.ConcurrentHashMap;

public class UsersReg {
    private boolean result;

    private static final ConcurrentHashMap<String,String> users = new ConcurrentHashMap<>();
    //хранит всех пользователей
    public boolean registerUser(String name , String password){
        if (!users.containsKey(name)){
            users.put(name,password);
            result = true;
        }
        else {
            result = false;
        }
        return result;
    }
    public boolean registerValet (String name , String password){
        if (users.containsKey(name)){
            if (users.get(name).equals(password)){
                result = true;
            }
        }
        else {
            result = false;
        }
        return result;
    }
}
