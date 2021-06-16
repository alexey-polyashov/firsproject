package common;

public interface Options {

    String adminPassword = "admin";

    String SERVER_HOST = "localhost";
    int SERVER_PORT = 8189;
    String SERVER_ROOT = "C:\\serverroot\\";
    int CHUNK_SIZE = 64000;//chunk size 64 kBytes

    String mail_serviceEmail = "filecloud.service@yandex.ru";
    String mail_smtpHost = "smtp.yandex.ru";
    String mail_smtpPort = "465";
    boolean mail_sslUsed = true;
    boolean mail_needAuthentication = true;
    String mail_login = "filecloud.service";
    String mail_password = "F1i2l3e4C5l6o7d8e9";

}
