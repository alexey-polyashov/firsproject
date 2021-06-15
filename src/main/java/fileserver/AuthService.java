package fileserver;

public class AuthService {

    private DBService dbService;

    public AuthService(DBService dbService) {
        this.dbService = dbService;
    }

    public User authUser(String login, String password) throws LoginFailException{
        User user = dbService.findUser(login, password);
        if(user == null){
            throw new LoginFailException();
        }
        return user;
    }

    public boolean registerNewUser(User newUser, String password) throws EmailIsUsedException, LoginIsUsedException {
        setNewUserFolder(newUser);
        return dbService.registerUser(newUser, password);
    }

    public boolean remaindPassword(User user){
        return true;
    }

    private void setNewUserFolder(User user){
        if(user.getFolder().isEmpty()){
            user.setFolder(user.getLogin());
        }
    }

}
