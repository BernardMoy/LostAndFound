package com.example.lostandfound;

import android.content.Context;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VerificationCodeManager {

    private String emailAddress;
    private FirestoreManager db;

    private static final String COLLECTION_NAME = "user_verifications";
    private static final int EMAIL_COOLDOWN = 60000;    // 1 minute
    private static final int VALID_TIME = 600000;    // 10 minutes

    public VerificationCodeManager(Context ctx, String emailAddress){
        this.emailAddress = emailAddress;
        db = new FirestoreManager(ctx);
    }

    // method to generate a 6 digit verification code and update the database data
    private String generateSixDigitCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);  // ensure 100000 <= code <= 999999

        return String.valueOf(code);
    }

    // method to generate a new verification code for the user, and return the code
    // only generates code if the last one is generated within 1 minute
    public void generateNewVerificationCode(CodeGenerationCallback codeGenerationCallback){
        db.get(COLLECTION_NAME, emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                // if user has generated a code before, check it
                if (result != null){
                    // check if the last code generated is within 1 minute from database given the current email
                    long storedTimeStamp = (long) result.get("timestamp");
                    long currentTimeStamp = Calendar.getInstance().getTimeInMillis();
                    if (currentTimeStamp - storedTimeStamp <= EMAIL_COOLDOWN){
                        // the last code is generated within the last minute
                        codeGenerationCallback.onCodeGenerated("Please wait for 1 minute before generating another code", "");
                        return;
                    }
                }

                // generate a new hashed code and update data in the database
                String code = generateSixDigitCode();
                String hashedCode = Hasher.hash(code);
                long currentTime = Calendar.getInstance().getTimeInMillis();

                Map<String, Object> data = new HashMap<>();
                data.put("hashedCode", hashedCode);
                data.put("timestamp", currentTime);

                // Update / create the entry in the database
                db.put(COLLECTION_NAME, emailAddress, data, new FirestoreManager.Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        if (!result){
                            codeGenerationCallback.onCodeGenerated("Error adding verification code to database", "");
                            return;
                        }

                        // end with no errors
                        codeGenerationCallback.onCodeGenerated("", code);
                    }
                });
            }
        });
    }

    // method to validate user's verification code
    public void validateVerificationCode(String givenCode, CodeVerificationCallback codeVerificationCallback){
        // get the code from user database
        db.get("user_verifications", emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                if (result == null) {
                    // no codes were generated
                    codeVerificationCallback.onCodeVerified("Please generate a new verification code");
                    return;
                }

                // check if the code is generated within the valid time
                long storedTimeStamp = (long) result.get("timestamp");
                long currentTimeStamp = Calendar.getInstance().getTimeInMillis();
                if (currentTimeStamp - storedTimeStamp > VALID_TIME) {
                    codeVerificationCallback.onCodeVerified("The code has expired, please generate a new verification code");
                    return;
                }

                // check if user's code is valid
                String storedHashedCode = (String) result.get("hashedCode");
                if (!Hasher.compareHash(givenCode, storedHashedCode)){
                    codeVerificationCallback.onCodeVerified("Invalid verification code");
                    return;
                }

                // everything passes, return true
                codeVerificationCallback.onCodeVerified("");
            }
        });
    }

}
