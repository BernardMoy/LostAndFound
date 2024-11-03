package com.example.lostandfound;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.BuildConfig;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Calendar;
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

    // cooldown time (in mills) for sending emails
    private final long EMAIL_COOLDOWN = 60000;   // 1 minute

    // time that the verification code is valid
    private final long VALID_TIME = 600000;   // 10 minutes


    // Create email sender object where emailAddress is the recipient
    public EmailSender(Context ctx, String emailAddress){
        this.ctx = ctx;
        this.emailAddress = emailAddress;

        // set up properties when the object is being created
        setUpProperties();

        // set up executor service
        executorService = Executors.newSingleThreadExecutor();
    }

    // main method to send email, and return true if user is not in cooldown.
    public void sendEmail(){
        ((Activity) ctx).runOnUiThread(() -> {
            executorService.submit(this::sendEmailInBackground);
        });
    }

    // method to send email to the stored emailAddress. Return true when email is sent successfully
    private boolean sendEmailInBackground(){
        String subject = ctx.getString(R.string.confirm_email_subject);

        // generate the code for the body of the email
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

    // method to generate a 6 digit verification code and update the sharedpreferences data
    private String generateVerificationCode(){
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);  // ensure 100000 <= code <= 999999

        // hash the generated code
        Hasher hasher = new Hasher();
        String hashedCode = hasher.hash(String.valueOf(code));

        // update shared preferences data
        long currentTime = Calendar.getInstance().getTimeInMillis();

        SharedPreferences sharedPreferences = ctx.getSharedPreferences("User_verification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("code", hashedCode);
        editor.putLong("timeStamp", currentTime);
        editor.putString("email", emailAddress);
        editor.apply();


        return String.valueOf(code);
    }

    // check if the user is currently in cooldown. If yes, they cannot receive another email.
    // This method should be checked in another activity that calls the send email method.
    public boolean isUserInCooldown(){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("User_verification", Context.MODE_PRIVATE);

        // get the email from sharedpreferences
        String storedEmail = sharedPreferences.getString("email", null);
        if (storedEmail == null || !storedEmail.equals(emailAddress)){
            // no emails are stored, or a different email is previously used
            return false;

        } else {
            // check if timestamp is at least 1 minute ago
            long prevTimeStamp = sharedPreferences.getLong("timeStamp", 0);
            long currentTimeStamp = Calendar.getInstance().getTimeInMillis();

            return (currentTimeStamp - prevTimeStamp < EMAIL_COOLDOWN);
        }
    }

    // verify if a code given to a user is valid. The code need to be generated in the last 10 minutes
    public boolean verifyCode(String code){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("User_verification", Context.MODE_PRIVATE);

        // get the email from sharedpreferences
        String storedEmail = sharedPreferences.getString("email", null);
        if (storedEmail == null || !storedEmail.equals(emailAddress)) {
            // no emails are stored, or a different email is previously used
            return false;

        } else {
            // check if code is generated more than 10 mins ago
            long prevTimeStamp = sharedPreferences.getLong("timeStamp", 0);
            long currentTimeStamp = Calendar.getInstance().getTimeInMillis();

            if (currentTimeStamp - prevTimeStamp > VALID_TIME){
                Toast.makeText(ctx, "This code has expired. Please generate another one", Toast.LENGTH_SHORT).show();
                return false;
            }

            // verify the code with the stored hashed code
            String storedHashCode = sharedPreferences.getString("code", "");
            Hasher hasher = new Hasher();
            return hasher.compareHash(code, storedHashCode);
        }
    }
}
