package fileserver;

public interface DBService {

    void InitDB();

    User getUserInfoByLogin(String login);

    User getUserInfoByEmail(String email);

    String getUserPasswordByEmail(String email);
    String getUserPasswordByLogin(String login);

    User findUser(String login, String password);

    boolean registerUser(User newUser, String password) throws LoginIsUsedException, EmailIsUsedException;

    boolean changeUserInfo(User userData);

    boolean changePassword(String login, String newPassword);


}
