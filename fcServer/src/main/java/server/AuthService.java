package server;


import common.Options;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class AuthService {

    private DBService dbService;

    public AuthService(DBService dbService) {
        this.dbService = dbService;
    }

    public User authUser(String login, String password) throws LoginFailException{
        log.debug("Invoke login for: {}, {}", login, password);
        User user = dbService.findUser(login, password);
        if(user == null){
            throw new LoginFailException();
        }
        return user;
    }

    public boolean registerNewUser(User newUser, String password) throws Exception {
        setNewUserFolder(newUser);
        return dbService.registerUser(newUser, password);
    }

    public boolean remindPassword(String email) throws Exception {

        String pwd = dbService.getUserPasswordByEmail(email);
        if(pwd.isEmpty()){
            throw new EmailNotFoundException("Email not found - " + email);
        }

        String text = "Hello!\n" +
                "You asked to remind you of your password in FileCloud service.\n" +
                "You password: " + pwd;
        String theme = "Remind password";
        sendEmailToUser(email, text, theme);

        return true;
    }

    private void sendEmailToUser(String email, String text, String theme) {

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", Options.mail_smtpHost);
        properties.setProperty("mail.smtp.user", Options.mail_login);
        properties.setProperty("mail.smtp.port", Options.mail_smtpPort);
        if(Options.mail_sslUsed) {
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        //properties.setProperty("mail.debug", "true");
        Session session;
        if(Options.mail_needAuthentication) {
            session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Options.mail_login, "F1i2l3e4C5l6o7d8e9");
                }
            });
        }else{
            session = Session.getDefaultInstance(properties);
        }

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(Options.mail_serviceEmail));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            // Set Subject: header field
            message.setSubject(theme);
            // Now set the actual message
            message.setText(text);
            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

    private void setNewUserFolder(User user){
        if(user.getFolder().isEmpty()){
            user.setFolder(user.getLogin());
        }
    }

    public boolean checkNewUser(common.UserInfo newUser) throws SQLException, EmailIsUsedException, LoginIsUsedException {
        return dbService.checkNewUser(newUser);
    }
}
