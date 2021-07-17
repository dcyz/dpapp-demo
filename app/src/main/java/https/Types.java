package https;

import java.util.Map;

public class Types {
    public static class User {
        private final String user;
        private final String passwd;

        public User(String user, String passwd) {
            this.user = user;
            this.passwd = passwd;
        }

        public String getUser(){
            return user;
        }

        public String getPasswd() {
            return passwd;
        }
    }

    public static class Result {
        private Map<String, ?> data;
        private String msg;
        private int status;

        public Map<String, ?> getData() {
            return data;
        }

        public String getMsg() {
            return msg;
        }

        public int getStatus() {
            return status;
        }
    }
}
