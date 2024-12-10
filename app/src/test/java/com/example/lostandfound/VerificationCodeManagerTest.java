package com.example.lostandfound;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class VerificationCodeManagerTest {
    @Mock
    private FirestoreManager mockFirestoreManager;

    private VerificationCodeManager verificationCodeManager;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

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
        verificationCodeManager.generateNewVerificationCode(new CodeGenerationCallback() {
            @Override
            public void onCodeGenerated(String error, String code) {
                assertEquals("", error);
                assertEquals(6, code.length());
                latch.countDown();
            }
        });
        latch.await();
    }
}