package server;

import java.sql.SQLException;

public class AuthService {

    DBService dbService;

    public AuthService(DBService dbService) {
        this.dbService = dbService;
    }

    public UserInfo loginUser(String name, String pass){
        try {
            return dbService.findUser(name, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserInfo changeNick(String login, String newNick) throws SQLException, NickIsBusy {
        if(newNick!=null && !newNick.trim().isEmpty()){
            return dbService.setNick(login, newNick);
        }
        return null;
    }

}
