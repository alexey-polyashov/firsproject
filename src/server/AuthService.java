package server;

import java.sql.SQLException;

public class AuthService {

    DBInterface dbService;

    public AuthService(DBInterface dbService) {
        this.dbService = dbService;
    }

    public UserInfo loginUser(String name, String pass){
        try {
            return dbService.findUser(name, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfo changeNick(String login, String newNick) throws Exception {
        if(newNick!=null && !newNick.trim().isEmpty()){
            return dbService.setNick(login, newNick);
        }
        return null;
    }

}
