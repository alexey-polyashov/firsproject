package server;

public interface AuthService {

    void start();
    void stop();
    String getNickByLogin(String login, String pass);
}
