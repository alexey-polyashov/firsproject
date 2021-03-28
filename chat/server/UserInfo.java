package chat.server;

public class UserInfo {

    private String name;
    private String nick;

    public UserInfo(String name, String nick) {
        this.name = name;
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public String getName() {
        return name;
    }
}
