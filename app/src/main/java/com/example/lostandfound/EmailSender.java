package com.example.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.BuildConfig;

import java.security.SecureRandom;
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
    private Context ctx;
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
        this.ctx = ctx;

        ((Activity) ctx).runOnUiThread(() -> {
            executorService.submit(this::sendEmailInBackground);
        });
    }

    // method to send email to the stored emailAddress. Return true when email is sent successfully
    private boolean sendEmailInBackground(){
        String subject = ctx.getString(R.string.confirm_email_subject);

        // the body of the email, including the generated code
        String code = generateVerificationCode();
        String body = code + " " + ctx.getString(R.string.confirm_email_body);

        String fromEmail = ctx.getString(R.string.sender_email);
        String fromPassword = ctx.getString(R.string.sender_password);
        String emailHost = ctx.getString(R.string.sender_host);

        // set up contents of the mime message
        try{
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
            mimeMessage.setSubject(subject);

            mimeMessage.setText(body);

            // Send email with Transport object
            Transport transport = newSession.getTransport("smtp");
            transport.connect(emailHost, fromEmail, fromPassword);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

        } catch (MessagingException e){
            // display failed sending email message
            Toast.makeText(ctx, "Email sending failed", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void setUpProperties(){
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");      // 587 for outlook emails
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        newSession = Session.getDefaultInstance(properties, null);

        mimeMessage = new MimeMessage(newSession);
    }

    // method to generate a 6 digit verification code
    private String generateVerificationCode(){
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);  // ensure 100000 <= code <= 999999

        return String.valueOf(code);
    }
}
