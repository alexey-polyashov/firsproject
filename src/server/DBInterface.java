package server;

import java.sql.*;
import java.util.concurrent.ExecutionException;

public interface DBInterface {

    public void close();

    public void createBase() throws Exception;

    public UserInfo findUser(String login, String pass) throws Exception;

    public UserInfo setNick(String login, String newNick) throws Exception;

}
