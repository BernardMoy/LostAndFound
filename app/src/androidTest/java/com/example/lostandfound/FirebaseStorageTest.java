package com.example.lostandfound;

import android.net.Uri;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager;
import com.google.firebase.database.collection.BuildConfig;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class FirebaseStorageTest {
    private Uri targetImage;
    private static final String TESTKEY = "ABCDEF0";
    private static final String TESTFOLDER = "Tests";
    private FirebaseStorageManager firebaseStorageManager;

    @Before
    public void setUp(){
        this.targetImage = Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image);
        this.firebaseStorageManager = new FirebaseStorageManager();
    }

    @Test
    public void testImage(){
        assert targetImage != null;
    }

    @Test
    public void testPutImage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        firebaseStorageManager.putImage(TESTFOLDER, TESTKEY, targetImage, new FirebaseStorageManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                // assert image uploading successful
                assert result;
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testGetImage() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);

        firebaseStorageManager.getImage(TESTFOLDER, TESTKEY, new FirebaseStorageManager.Callback<Uri>() {
            @Override
            public void onComplete(Uri result) {
                assert result == targetImage;
                latch.countDown();
            }
        });

        latch.await();
    }

    @Test
    public void testDeleteImage() throws InterruptedException{
        final CountDownLatch latch = new CountDownLatch(1);

        firebaseStorageManager.deleteImage(TESTFOLDER, TESTKEY, new FirebaseStorageManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                assert result;      // delete successful
                latch.countDown();
            }
        });

        latch.await();

        final CountDownLatch latch2 = new CountDownLatch(1);
        firebaseStorageManager.getImage(TESTFOLDER, TESTKEY, new FirebaseStorageManager.Callback<Uri>() {
            @Override
            public void onComplete(Uri result) {
                assert result == null;
                latch2.countDown();
            }
        });

        latch2.await();
    }
}
