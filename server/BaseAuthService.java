package server;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {

    class UserDescribe {
        private String login;
        private String password;
        private String nick;

        public UserDescribe(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    List<UserDescribe> users;

    public BaseAuthService() {
        users = new ArrayList<>();
        users.add(new UserDescribe("f", "f", "FIRST"));
        users.add(new UserDescribe("s", "s", "SECOND"));
        users.add(new UserDescribe("t", "t", "THIRD"));
    }

    @Override
    public void start() {
        System.out.println("Auth service started");
    }

    @Override
    public void stop() {
        System.out.println("Auth service stopped");
    }

    @Override
    public String getNickByLogin(String login, String pass) {
         UserDescribe u = users.stream().filter((o)-> o.login.equals(login) && o.password.equals(pass) ).findFirst().orElse(null);
         if(u != null){
            return u.nick;
         } else {
            return null;
         }
    }
}
