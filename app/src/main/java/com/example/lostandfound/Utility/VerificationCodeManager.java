package com.example.lostandfound.Utility;

import com.example.lostandfound.Data.FirebaseNames;
import com.example.lostandfound.FirebaseManagers.FirestoreManager;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VerificationCodeManager {

    private final String emailAddress;
    private final FirestoreManager db;

    private static final int EMAIL_COOLDOWN = 60000;    // 1 minute
    private static final int VALID_TIME = 600000;    // 10 minutes

    public interface CodeGenerationCallback {
        // if there are errors, code is empty
        // if there are no errors, error is empty
        void onCodeGenerated(String error, String code);
    }


    public VerificationCodeManager(String emailAddress) {
        this.emailAddress = emailAddress;
        this.db = new FirestoreManager();
    }

    public VerificationCodeManager(String emailAddress, FirestoreManager db) {
        this.emailAddress = emailAddress;
        this.db = db;
    }

    // method to generate a 6 digit verification code and update the database data
    private String generateSixDigitCode() {
        SecureRandom secureRandom = new SecureRandom();
        int code = 100000 + secureRandom.nextInt(900000);  // ensure 100000 <= code <= 999999

        return String.valueOf(code);
    }

    // method to generate a new verification code for the user, and return the code
    // only generates code if the last one is generated within 1 minute
    public void generateNewVerificationCode(CodeGenerationCallback codeGenerationCallback) {
        db.get(FirebaseNames.COLLECTION_USER_VERIFICATIONS, emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                // if user has generated a code before, check it
                if (result != null) {
                    // check if the last code generated is within 1 minute from database given the current email
                    long storedTimeStamp = (long) result.get(FirebaseNames.USER_VERIFICATIONS_TIMESTAMP);
                    long currentTimeStamp = Calendar.getInstance().getTimeInMillis();
                    if (currentTimeStamp - storedTimeStamp <= EMAIL_COOLDOWN) {
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
                data.put(FirebaseNames.USER_VERIFICATIONS_HASHEDCODE, hashedCode);
                data.put(FirebaseNames.USER_VERIFICATIONS_TIMESTAMP, currentTime);

                // Update / create the entry in the database
                db.put(FirebaseNames.COLLECTION_USER_VERIFICATIONS, emailAddress, data, new FirestoreManager.Callback<Boolean>() {
                    @Override
                    public void onComplete(Boolean result) {
                        if (!result) {
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
    public void validateVerificationCode(String givenCode, ErrorCallback codeVerificationCallback) {
        // get the code from user database
        db.get(FirebaseNames.COLLECTION_USER_VERIFICATIONS, emailAddress, new FirestoreManager.Callback<Map<String, Object>>() {
            @Override
            public void onComplete(Map<String, Object> result) {
                if (result == null) {
                    // no codes were generated
                    codeVerificationCallback.onComplete("Please generate a new verification code");
                    return;
                }

                // check if the code is generated within the valid time
                long storedTimeStamp = (long) result.get(FirebaseNames.USER_VERIFICATIONS_TIMESTAMP);
                long currentTimeStamp = Calendar.getInstance().getTimeInMillis();
                if (currentTimeStamp - storedTimeStamp > VALID_TIME) {
                    codeVerificationCallback.onComplete("The code has expired, please generate a new verification code");
                    return;
                }

                // check if user's code is valid
                String storedHashedCode = (String) result.get(FirebaseNames.USER_VERIFICATIONS_HASHEDCODE);
                if (!Hasher.compareHash(givenCode, storedHashedCode)) {
                    codeVerificationCallback.onComplete("Invalid verification code");
                    return;
                }

                // everything passes, return true
                codeVerificationCallback.onComplete("");
            }
        });
    }

}
