package com.example.lostandfound.Utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.lostandfound.BuildConfig;
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

/*
Utility methods to send emails using Java Mail API
for the 6 digit register code
 */
public class EmailSender {
    private final Context ctx;
    private final String emailAddress;
    private MimeMessage mimeMessage;
    private Session newSession;
    private final ExecutorService executorService;

    public interface EmailCallback {
        void onComplete(Boolean success);
    }


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
    public void sendEmail(String subject, String body, boolean hasToastMessage, EmailCallback callback) {

        // Send email in a separate thread
        executorService.execute(() -> {

            // get sender details
            String fromEmail = BuildConfig.SENDER_EMAIL;
            String fromPassword = BuildConfig.SENDER_PASSWORD;
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
                if (hasToastMessage) {
                    ((Activity) ctx).runOnUiThread(() ->
                            Toast.makeText(ctx, "Email successfully sent.", Toast.LENGTH_SHORT).show()
                    );

                    // return true
                    callback.onComplete(true);

                } else {
                    // just return true
                    callback.onComplete(true);
                }

            } catch (MessagingException e) {
                // display failed sending email message
                Log.d("EMAIL SENDING ERROR", e.getMessage());
                ((Activity) ctx).runOnUiThread(() ->
                        Toast.makeText(ctx, "Email sending failed", Toast.LENGTH_SHORT).show()
                );

                callback.onComplete(false);
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
