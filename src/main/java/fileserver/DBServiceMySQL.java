package fileserver;


import common.Options;
import common.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DBServiceMySQL implements DBService {

    private Connection connection;
    private Statement statement;

    public DBServiceMySQL() {

        try {

            log.debug("Start DB service");

            connection = null;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Users.s2db");
            statement = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            log.error("DB Error: {}", e.toString());
        }
    }

    private void createBase() throws SQLException {

        statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE if not exists 'Users'" +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text, 'password' text, 'email' text, 'userdir' text);");

        try(
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Users (login, password, email, userdir)\n" +
                "SELECT * FROM (SELECT 'admin', '" + Options.adminPassword + "', 'polyashofff@yandex.ru', 'admin')\n" +
                "WHERE NOT EXISTS (\n" +
                "    SELECT login FROM Users WHERE login = 'admin'\n" +
                ") LIMIT 1")
        ){
            ps.executeUpdate();
        }

        ResultSet resultSet = statement.executeQuery("SELECT * FROM Users");
        while(resultSet.next()){
            System.out.printf("Record: login - %s, email - %s, folder - %s\n",
                    resultSet.getString("login"),
                    resultSet.getString("email"),
                    resultSet.getString("userdir"));
        }

    }

    @Override
    public void InitDB() {
        try {
            log.debug("Create database");
            createBase();
        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
            throwables.printStackTrace();
        }
    }

    @Override
    public User getUserInfoByLogin(String login) {

        ResultSet resultSet = null;
        try {
                resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ login + "'");

            if(resultSet.next()){
                User userInfo = new User(resultSet.getString("login"), resultSet.getString("email"), resultSet.getString("userdir"));
                return userInfo;
            }
        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
        }
        return null;

    }

    @Override
    public User getUserInfoByEmail(String email) {

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE email = '"+ email + "'");

            if(resultSet.next()){
                User userInfo = new User(resultSet.getString("login"), resultSet.getString("email"), resultSet.getString("userdir"));
                return userInfo;
            }
        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
        }
        return null;

    }

    @Override
    public String getUserPasswordByEmail(String email) throws Exception {

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE email = '"+ email + "'");

            if(resultSet.next()){
                return resultSet.getString("password");
            }
        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
            throw throwables;
        }
        return "";

    }

    @Override
    public String getUserPasswordByLogin(String login) {

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ login + "'");

            if(resultSet.next()){
                return resultSet.getString("password");
            }
        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
        }
        return "";

    }
    @Override
    public User findUser(String login, String password) {

        ResultSet resultSet = null;
        try {

            resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ login + "'" + " AND password = '" + password + "'");

            if(resultSet.next()){
                User userInfo = new User(resultSet.getString("login"), resultSet.getString("email"), resultSet.getString("userdir"));
                return userInfo;
            }
        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
        }
        return null;

    }

    @Override
    public boolean registerUser(User newUser, String password) throws Exception{

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ newUser.getLogin() + "'");
            if(resultSet.next()){
                throw new LoginIsUsedException();
            }
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE email = '"+ newUser.getEmail() + "'");
            if(resultSet.next()){
                throw new EmailIsUsedException();
            }

            try(PreparedStatement ps = connection.prepareStatement("INSERT INTO Users (login, password, email, userdir) VALUES (?,?,?,?)")){
                ps.setString(1,newUser.getLogin());
                ps.setString(2,password);
                ps.setString(3,newUser.getEmail());
                ps.setString(4,newUser.getFolder());

                ps.executeUpdate();

            }catch(SQLException e){
                log.error("DB Error: {}", e.toString());
                throw e;
            }

        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
            throw throwables;
        }

        return true;
    }

    @Override
    public boolean changeUserInfo(User userData) throws Exception {

        ResultSet resultSet = statement.executeQuery("SELECT * FROM Users WHERE email = '"+ userData.getEmail() + "'");
        if(resultSet.next()){
            throw new EmailIsUsedException();
        }
        statement.executeUpdate("UPDATE Users SET email='" + userData.getEmail() + "' WHERE login = '"+ userData.getLogin() + "'");
        return true;

    }

    @Override
    public boolean changePassword(String login, String newPassword) throws Exception {

       statement.executeUpdate("UPDATE Users SET password='" + newPassword + "' WHERE login = '"+ login + "'");
       return true;

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

    @Override
    public boolean checkNewUser(UserInfo newUser) throws LoginIsUsedException, EmailIsUsedException, SQLException {

        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ newUser.getLogin() + "'");
            if(resultSet.next()){
                throw new LoginIsUsedException();
            }
            resultSet = statement.executeQuery("SELECT * FROM Users WHERE email = '"+ newUser.getEmail() + "'");
            if(resultSet.next()){
                throw new EmailIsUsedException();
            }

        } catch (SQLException throwables) {
            log.error("DB Error: {}", throwables.toString());
            throw throwables;
        }

        return true;

    }

    private List<UserRecord> userTable = new ArrayList<>();

}
