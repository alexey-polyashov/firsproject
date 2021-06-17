package server;

public class User {

    private String login;
    private String email;
    private String folder;

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getFolder() {
        return folder;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public User(String login, String email, String folder) {
        this.login = login;
        this.email = email;
        this.folder = folder;
    }


}
