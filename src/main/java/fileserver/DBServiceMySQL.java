package fileserver;


import java.util.ArrayList;
import java.util.List;

public class DBServiceMySQL implements DBService {

    @Override
    public void InitDB() {
        userTable.add(new UserRecord("admin", "admin", "polyashofff@yandex.ru", "admin"));
        userTable.add(new UserRecord("user1", "user1", "user1@yandex.ru", "user1"));
    }

    @Override
    public User getUserInfoByLogin(String login) {
        for (UserRecord rec:userTable) {
            if(rec.login.equals(login)){
                return new User(rec.login, rec.email, rec.userDir);
            }
        }
        return null;
    }

    @Override
    public User getUserInfoByEmail(String email) {
        for (UserRecord rec:userTable) {
            if(rec.email.equals(email)){
                return new User(rec.login, rec.email, rec.userDir);
            }
        }
        return null;
    }

    @Override
    public String getUserPasswordByEmail(String email) {
        for (UserRecord rec:userTable) {
            if(rec.email.equals(email)){
                return rec.password;
            }
        }
        return "";
    }

    @Override
    public String getUserPasswordByLogin(String login) {
        for (UserRecord rec:userTable) {
            if(rec.login.equals(login)){
                return rec.password;
            }
        }
        return "";
    }
    @Override
    public User findUser(String login, String password) {
        for (UserRecord rec:userTable) {
            if(rec.login.equals(login) && rec.password.equals(password)){
                return new User(rec.login, rec.email, rec.userDir);
            }
        }
        return null;
    }

    @Override
    public boolean registerUser(User newUser, String password) throws LoginIsUsedException, EmailIsUsedException{
        for (UserRecord rec:userTable) {
            if(rec.login.equals(newUser.getLogin())){
                throw new LoginIsUsedException();
            } else if(rec.login.equals(newUser.getEmail())){
                throw new EmailIsUsedException();
            }
        }
        userTable.add(new UserRecord(
                newUser.getLogin(),
                password,
                newUser.getEmail(),
                newUser.getFolder()));
        return true;
    }

    @Override
    public boolean changeUserInfo(User userData) {
        return false;
    }

    @Override
    public boolean changePassword(String login, String newPassword) {
        return false;
    }


    private class UserRecord {

        public String login;
        public String password;
        public String email;
        public String userDir;

        public UserRecord(String login, String password, String email, String userDir) {
            this.login = login;
            this.password = password;
            this.email = email;
            this.userDir = userDir;
        }
    }

    private List<UserRecord> userTable = new ArrayList<>();

}
