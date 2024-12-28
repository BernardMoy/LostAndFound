package com.example.lostandfound.Utility;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.lostandfound.R;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailSender {
    private Context ctx;
    private String emailAddress;
    private MimeMessage mimeMessage;
    private Session newSession;
    private ExecutorService executorService;

    // Create email sender object where emailAddress is the recipient
    public EmailSender(Context ctx, String emailAddress) {
        this.ctx = ctx;
        this.emailAddress = emailAddress;

        // set up properties when the object is being created
        setUpProperties();

        // set up executor service
        executorService = Executors.newSingleThreadExecutor();
    }


    // method to send email to the stored emailAddress.
    // param isRegenerated: If true, this will generate a toast message when email is sent
    public void sendEmail(String subject, String body, boolean hasToastMessage) {

        // Send email in a separate thread
        executorService.execute(() -> {

            // get sender details
            String fromEmail = ctx.getString(R.string.sender_email);
            String fromPassword = ctx.getString(R.string.sender_password);
            String emailHost = ctx.getString(R.string.sender_host);

            // send the email
            try {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);

                // Send email with Transport object
                Transport transport = newSession.getTransport("smtp");
                transport.connect(emailHost, fromEmail, fromPassword);
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

                // display successful message if requested
                if(hasToastMessage) {
                    ((Activity) ctx).runOnUiThread(() ->
                            Toast.makeText(ctx, "Email successfully sent.", Toast.LENGTH_SHORT).show()
                    );
                }

            } catch (MessagingException e) {
                // display failed sending email message
                ((Activity) ctx).runOnUiThread(() ->
                        Toast.makeText(ctx, "Email sending failed", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // method to set up mail sending properties, which is to an outlook account
    private void setUpProperties() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");      // 587 for outlook emails
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);

        newSession = Session.getDefaultInstance(properties, null);

        mimeMessage = new MimeMessage(newSession);
    }
}
