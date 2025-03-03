package com.example.lostandfound;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.lostandfound.Utility.EmailSender;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class EmailSenderTest {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testSendEmail() throws InterruptedException {
        // email credentials
        String subject = "Test Email";
        String body = "This is a test";

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean res = new AtomicBoolean(false);

        // create email sender
        EmailSender emailSender = new EmailSender(
                context,
                "testtestthisemaildoesnotexist32988923@warwick.ac.uk"
        );
        emailSender.sendEmail(subject, body, false, new EmailSender.EmailCallback() {
            @Override
            public void onComplete(Boolean success) {
                res.set(success);
                latch.countDown();
            }
        });

        latch.await();
        Thread.sleep(2000);

        // assert send email successful
        assertTrue(res.get());

    }
}
