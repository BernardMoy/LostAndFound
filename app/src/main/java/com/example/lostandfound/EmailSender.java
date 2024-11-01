package com.example.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailSender {
    private String emailAddress;
    private MimeMessage mimeMessage;
    private Session newSession;
    private ExecutorService executorService;

    public EmailSender(String emailAddress){
        this.emailAddress = emailAddress;

        // set up properties when the object is being created
        setUpProperties();

        // set up executor service
        executorService = Executors.newSingleThreadExecutor();
    }

    // main method to send email
    public void sendEmail(Context ctx){
        ((Activity) ctx).runOnUiThread(() -> {
            executorService.submit(this::sendEmailInBackground);
        });
    }

    // method to send email to the stored emailAddress. Return true when email is sent successfully
    private boolean sendEmailInBackground(){
        String subject = "Confirm your email";
        String body = "OWO";

        String fromEmail = "lostfoundapp3@gmail.com";
        String fromPassword = "srtx rvkp lpyc kwos";
        String emailHost = "smtp.gmail.com";

        // set up contents of the mime message
        try{
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
            mimeMessage.setSubject(subject);

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(body, "html/text");

            MimeMultipart multiPart = new MimeMultipart();
            multiPart.addBodyPart(bodyPart);

            mimeMessage.setContent(multiPart);

            // Send email with Transport object
            Transport transport = newSession.getTransport("smtp");
            transport.connect(emailHost, fromEmail, fromPassword);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

        } catch (MessagingException e){
            Log.d("EMAIL BUG", e.getMessage());
            return false;
        }


        return true;
    }

    private void setUpProperties(){
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        newSession = Session.getDefaultInstance(properties, null);

        mimeMessage = new MimeMessage(newSession);
    }
}
