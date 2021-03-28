package chat.server;

import java.sql.*;

public class SQLService implements DBInterface{

    private Connection connection;
    private Statement statement;

    public void close(){
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SQLService() {

        try {

            connection = null;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Users.s2db");
            statement = connection.createStatement();
            createBase();


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void createBase() throws SQLException {

        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'Users'" +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'nick' text, 'login' text, 'password' text);");

        try(PreparedStatement ps = connection.prepareStatement("INSERT INTO Users (nick, login, password) VALUES (?,?,?)")){
            ps.setString(1,"Alex");
            ps.setString(2,"Alexey");
            ps.setString(3,"123");
            ps.addBatch();

            ps.setString(1,"Bobby");
            ps.setString(2,"Bob");
            ps.setString(3,"123");
            ps.addBatch();

            ps.setString(1,"Nicky");
            ps.setString(2,"Nick");
            ps.setString(3,"123");
            ps.addBatch();

            ps.executeBatch();

        }

    }

    public UserInfo findUser(String login, String pass) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ login + "' AND password = '" + pass+"'");
        if(resultSet.next()){
            UserInfo userInfo = new UserInfo(resultSet.getString("login"), resultSet.getString("nick"));
            return userInfo;
        }
        return null;
    }

    public UserInfo setNick(String login, String newNick) throws SQLException, NickIsBusy {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Users WHERE nick = '"+ newNick + "'");
        if(resultSet.next()){
            throw new NickIsBusy();
        }
        statement.executeUpdate("UPDATE Users SET nick='" + newNick + "' WHERE login = '"+ login + "'");
        resultSet = statement.executeQuery("SELECT * FROM Users WHERE login = '"+ login + "'");
        if(resultSet.next()){
            UserInfo userInfo = new UserInfo(resultSet.getString("login"), resultSet.getString("nick"));
            return userInfo;
        }
        return null;
    }

}
