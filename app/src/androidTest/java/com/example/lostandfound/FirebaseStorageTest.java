package com.example.lostandfound;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class FirebaseStorageTest {
    private Uri targetImage;
    private static final String TESTKEY = "ABCDEF0";
    private FirebaseStorageManager firebaseStorageManager;

    @Before
    public void setUp(){
        this.targetImage = Uri.parse("app/src/main/res/drawable/placeholder_image.png");
        this.firebaseStorageManager = new FirebaseStorageManager();
    }

    @Test
    public void testImage(){
        assert targetImage != null;
    }

    @Test
    public void testPutImage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        firebaseStorageManager.putImage(TESTKEY, targetImage, new FirebaseStorageManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                // assert image uploading successful
                assert result;
                latch.countDown();
            }
        });

        latch.await();
    }
}
