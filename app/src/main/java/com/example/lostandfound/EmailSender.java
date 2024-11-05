package com.example.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Map;
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

    private FirestoreManager db;

    // cooldown time (in mills) for sending emails
    private final long EMAIL_COOLDOWN = 60000;   // 1 minute


    // the collection name used to store user code data
    private final String COLLECTION_NAME = "user_verifications";
    private long storedTimeStamp = 0;


    // Create email sender object where emailAddress is the recipient
    public EmailSender(Context ctx, String emailAddress) {
        this.ctx = ctx;
        this.emailAddress = emailAddress;

        // set up properties when the object is being created
        setUpProperties();

        // set up executor service
        executorService = Executors.newSingleThreadExecutor();

        // set up firestore manager
        db = new FirestoreManager(ctx);
    }


    // method to send email to the stored emailAddress.
    public void sendEmail(boolean isRegenerated) {

        // get the data from firestore with user's current email
        db.getValue(COLLECTION_NAME, emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {

                // check if the last email sent is not within 1 minute
                // if there is a record of the user previously generating an email
                if (result != null){
                    storedTimeStamp = (long) result.get("timestamp");   // get the stored timestamp from db
                    long currentTimeStamp = Calendar.getInstance().getTimeInMillis();
                    if (currentTimeStamp - storedTimeStamp <= EMAIL_COOLDOWN){
                        Toast.makeText(ctx, "Please wait for 1 minute before sending another email.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // begin sending email and generate code in a separate thread
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    String subject = ctx.getString(R.string.confirm_email_subject);

                    // generate the code for the body of the email
                    String code = generateVerificationCode();
                    String body = code + " " + ctx.getString(R.string.confirm_email_body);

                    String fromEmail = ctx.getString(R.string.sender_email);
                    String fromPassword = ctx.getString(R.string.sender_password);
                    String emailHost = ctx.getString(R.string.sender_host);

                    // set up contents of the mime message
                    try {
                        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
                        mimeMessage.setSubject(subject);

                        mimeMessage.setText(body);

                        // Send email with Transport object
                        Transport transport = newSession.getTransport("smtp");
                        transport.connect(emailHost, fromEmail, fromPassword);
                        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());

                        // display successful message
                        if (isRegenerated){
                            ((Activity) ctx).runOnUiThread(() ->
                                    Toast.makeText(ctx, "A new confirmation email has been sent.", Toast.LENGTH_SHORT).show()
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

    // method to generate a 6 digit verification code and update the database data
    private String generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);  // ensure 100000 <= code <= 999999

        // hash the generated code
        Hasher hasher = new Hasher();
        String hashedCode = hasher.hash(String.valueOf(code));

        // update data in database
        long currentTime = Calendar.getInstance().getTimeInMillis();
        VerificationData data = new VerificationData(hashedCode, currentTime);

        db.put(COLLECTION_NAME, emailAddress, data, new FirestoreManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {

            }
        });

        // return the original (unhashed) code
        return String.valueOf(code);
    }
}
