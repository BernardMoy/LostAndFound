package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.example.lostandfound.FirebaseManagers.FirestoreManager;
import com.example.lostandfound.Utility.FirestoreNames;
import com.example.lostandfound.Utility.Hasher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


public class VerificationCodeManagerTest {
    @Mock
    private FirestoreManager mockFirestoreManager;

    private VerificationCodeManager verificationCodeManager;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    // test when user havent generate code before
    @Test
    public void testGenerateVerificationCodeSuccess() throws InterruptedException{
        String testEmail = "test@warwick.ac.uk";
        verificationCodeManager = new VerificationCodeManager(testEmail, mockFirestoreManager);

        // simulate when the get method is used on this email, it returns null
        // indicating the user has never generated a code before
        doAnswer(invocation -> {
            FirestoreManager.Callback<Map<String, Object>> callback = invocation.getArgument(2);
            callback.onComplete(null);      // user data not found in db
            return null;
        }).when(mockFirestoreManager).get(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(FirestoreManager.Callback.class));

        // mock the put method to the database after the code is generated
        doAnswer(invocation -> {
            FirestoreManager.Callback<Boolean> callback = invocation.getArgument(3);
            callback.onComplete(true);      // assume put always return true
            return null;
        }).when(mockFirestoreManager).put(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(Map.class), any(FirestoreManager.Callback.class));


        // code generation should be successful, returning no errors
        final CountDownLatch latch = new CountDownLatch(1);
        verificationCodeManager.generateNewVerificationCode(new VerificationCodeManager.CodeGenerationCallback() {
            @Override
            public void onCodeGenerated(String error, String code) {
                assertEquals("", error);
                assertEquals(6, code.length());
                latch.countDown();
            }
        });
        latch.await();
    }

    // test when user generated code very long ago
    @Test
    public void testGenerateVerificationCodeSuccess2() throws InterruptedException{
        String testEmail = "test@warwick.ac.uk";
        verificationCodeManager = new VerificationCodeManager(testEmail, mockFirestoreManager);

        // simulate when the get method is used on this email, it returns code with timestamp = 0
        // indicating the user has generated a code very long ago
        doAnswer(invocation -> {
            FirestoreManager.Callback<Map<String, Object>> callback = invocation.getArgument(2);

            Map<String, Object> result = new HashMap<>();
            result.put(FirestoreNames.USER_VERIFICATIONS_HASHEDCODE, "000000");
            result.put(FirestoreNames.USER_VERIFICATIONS_TIMESTAMP, 0L);

            callback.onComplete(result);
            return null;
        }).when(mockFirestoreManager).get(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(FirestoreManager.Callback.class));

        // mock the put method to the database after the code is generated
        doAnswer(invocation -> {
            FirestoreManager.Callback<Boolean> callback = invocation.getArgument(3);
            callback.onComplete(true);      // assume put always return true
            return null;
        }).when(mockFirestoreManager).put(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(Map.class), any(FirestoreManager.Callback.class));

        // code generation should be successful, returning no errors
        final CountDownLatch latch = new CountDownLatch(1);
        verificationCodeManager.generateNewVerificationCode(new VerificationCodeManager.CodeGenerationCallback() {
            @Override
            public void onCodeGenerated(String error, String code) {
                assertEquals("", error);
                assertEquals(6, code.length());
                latch.countDown();
            }
        });
        latch.await();
    }


    // test when user generated code recently, an error message should appear
    @Test
    public void testGenerateVerificationCodeFail() throws InterruptedException{
        String testEmail = "test@warwick.ac.uk";
        verificationCodeManager = new VerificationCodeManager(testEmail, mockFirestoreManager);

        // simulate when the get method is used on this email, it returns code adn timestamp = current time -30s
        // the user should not be able to generate a new code
        doAnswer(invocation -> {
            FirestoreManager.Callback<Map<String, Object>> callback = invocation.getArgument(2);

            Map<String, Object> result = new HashMap<>();
            result.put(FirestoreNames.USER_VERIFICATIONS_HASHEDCODE, "000000");
            long testTimeStamp = Calendar.getInstance().getTimeInMillis() - 30*1000;
            result.put(FirestoreNames.USER_VERIFICATIONS_TIMESTAMP, testTimeStamp);

            callback.onComplete(result);
            return null;
        }).when(mockFirestoreManager).get(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(FirestoreManager.Callback.class));

        // mock the put method to the database after the code is generated
        doAnswer(invocation -> {
            FirestoreManager.Callback<Boolean> callback = invocation.getArgument(3);
            callback.onComplete(true);      // assume put always return true
            return null;
        }).when(mockFirestoreManager).put(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(Map.class), any(FirestoreManager.Callback.class));

        // code generation should be successful, returning no errors
        final CountDownLatch latch = new CountDownLatch(1);
        verificationCodeManager.generateNewVerificationCode(new VerificationCodeManager.CodeGenerationCallback() {
            @Override
            public void onCodeGenerated(String error, String code) {
                assertEquals("Please wait for 1 minute before generating another code", error);
                assertEquals("", code);
                latch.countDown();
            }
        });
        latch.await();
    }

    // method to test success validation of the code (Within time limit and correct)
    @Test
    public void testValidateVerificationCodeSuccess() throws InterruptedException{
        String testEmail = "test@warwick.ac.uk";
        verificationCodeManager = new VerificationCodeManager(testEmail, mockFirestoreManager);

        // simulate when the get method is used on this email, it returns code adn timestamp = current time - 300 seconds
        // the user should not be able to generate a new code
        doAnswer(invocation -> {
            FirestoreManager.Callback<Map<String, Object>> callback = invocation.getArgument(2);

            Map<String, Object> result = new HashMap<>();
            result.put(FirestoreNames.USER_VERIFICATIONS_HASHEDCODE, Hasher.hash("328334"));    // stores the hashed code
            long testTimeStamp = Calendar.getInstance().getTimeInMillis() - 5*60*1000;
            result.put(FirestoreNames.USER_VERIFICATIONS_TIMESTAMP, testTimeStamp);

            callback.onComplete(result);
            return null;

        }).when(mockFirestoreManager).get(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(FirestoreManager.Callback.class));

        // provide the correct verification code
        final CountDownLatch latch = new CountDownLatch(1);
        verificationCodeManager.validateVerificationCode("328334", new ErrorCallback() {
            @Override
            public void onComplete(String error) {
                assertEquals("", error);     // no errors
                latch.countDown();
            }
        });

        latch.await();
    }

    // method to test fail validation, due to the time being more than 10 mins ago
    @Test
    public void testValidateVerificationCodeFailTime() throws InterruptedException{
        String testEmail = "test@warwick.ac.uk";
        verificationCodeManager = new VerificationCodeManager(testEmail, mockFirestoreManager);

        // simulate when the get method is used on this email, it returns code adn timestamp = current time - 5 mins
        // the user should not be able to generate a new code
        doAnswer(invocation -> {
            FirestoreManager.Callback<Map<String, Object>> callback = invocation.getArgument(2);

            Map<String, Object> result = new HashMap<>();
            result.put(FirestoreNames.USER_VERIFICATIONS_HASHEDCODE, Hasher.hash("328334"));    // stores the hashed code
            long testTimeStamp = Calendar.getInstance().getTimeInMillis() - 5*60*1000;
            result.put(FirestoreNames.USER_VERIFICATIONS_TIMESTAMP, testTimeStamp);

            callback.onComplete(result);
            return null;

        }).when(mockFirestoreManager).get(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(FirestoreManager.Callback.class));

        // provide the correct verification code
        final CountDownLatch latch = new CountDownLatch(1);
        verificationCodeManager.validateVerificationCode("328314", new ErrorCallback() {
            @Override
            public void onComplete(String error) {
                assertEquals("Invalid verification code", error);     // no errors
                latch.countDown();
            }
        });

        latch.await();
    }

    // method to test fail validation, due to the code being incorrect
    @Test
    public void testValidateVerificationCodeFailCode() throws InterruptedException{
        String testEmail = "test@warwick.ac.uk";
        verificationCodeManager = new VerificationCodeManager(testEmail, mockFirestoreManager);

        // simulate when the get method is used on this email, it returns code adn timestamp = current time - 11 mins
        // the user should not be able to generate a new code
        doAnswer(invocation -> {
            FirestoreManager.Callback<Map<String, Object>> callback = invocation.getArgument(2);

            Map<String, Object> result = new HashMap<>();
            result.put(FirestoreNames.USER_VERIFICATIONS_HASHEDCODE, Hasher.hash("328334"));    // stores the hashed code
            long testTimeStamp = Calendar.getInstance().getTimeInMillis() - 11*60*1000;
            result.put(FirestoreNames.USER_VERIFICATIONS_TIMESTAMP, testTimeStamp);

            callback.onComplete(result);
            return null;

        }).when(mockFirestoreManager).get(eq(FirestoreNames.COLLECTION_USER_VERIFICATIONS), eq(testEmail), any(FirestoreManager.Callback.class));

        // provide the correct verification code
        final CountDownLatch latch = new CountDownLatch(1);
        verificationCodeManager.validateVerificationCode("328334", new ErrorCallback() {
            @Override
            public void onComplete(String error) {
                assertEquals("The code has expired, please generate a new verification code", error);     // no errors
                latch.countDown();
            }
        });

        latch.await();
    }
}