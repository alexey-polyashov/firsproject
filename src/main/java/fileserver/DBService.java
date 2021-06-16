package fileserver;

import common.UserInfo;

import java.sql.SQLException;

public interface DBService {

    void InitDB();

    User getUserInfoByLogin(String login);

    User getUserInfoByEmail(String email);

    String getUserPasswordByEmail(String email) throws SQLException, Exception;
    String getUserPasswordByLogin(String login);

    User findUser(String login, String password);

    boolean registerUser(User newUser, String password) throws Exception;

    boolean changeUserInfo(User userData) throws SQLException, Exception;

    boolean changePassword(String login, String newPassword) throws SQLException, Exception;


    boolean checkNewUser(UserInfo newUser) throws LoginIsUsedException, EmailIsUsedException, SQLException;

}
