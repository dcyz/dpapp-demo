package gson;

public class User {
    private final String user;
    private final String passwd;

    public User(String user, String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public String getUser() {
        return user;
    }

    public String getPasswd() {
        return passwd;
    }
}
