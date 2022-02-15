package com.epias.Mobile.utils;

import com.epias.Mobile.helper.StoreHelper;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JavaMail {
    public static void sendMail(String recepient) throws Exception {
        System.out.println("Preparing to send mail");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String myAccountEmail = "musicayy.@gmail.com";
        String pw = "********";

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, pw);
            }
        });
        Message msg = prepareMessage(session, myAccountEmail, recepient);
        Transport.send(msg);
        System.out.println("Message sent successfully");
    }

    private static Message prepareMessage(Session session, String myAccountEmail, String receipent) {

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(myAccountEmail));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(receipent));
            msg.setSubject("Epias Mobile Automation");
            msg.setText("Arithmetic Average Value : " + StoreHelper.INSTANCE.getValue("avg"));
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
