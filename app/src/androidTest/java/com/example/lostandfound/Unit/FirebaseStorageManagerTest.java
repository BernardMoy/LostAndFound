package com.example.lostandfound.Unit;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.lostandfound.FirebaseManagers.FirebaseStorageManager;
import com.example.lostandfound.FirebaseTestsSetUp;
import com.example.lostandfound.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Before;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class FirebaseStorageManagerTest extends FirebaseTestsSetUp {
    private Uri targetImage;
    private static final String TESTKEY = "ABCDEF0";
    private static final String TESTFOLDER = "Tests";
    private FirebaseStorageManager firebaseStorageManager;

    private static final FirebaseAuth auth = getAuth();
    private static final FirebaseStorage storage = getStorage();

    @Before
    public void setUp() throws InterruptedException {
        this.targetImage = Uri.parse("android.resource://com.example.lostandfound/" + R.drawable.placeholder_image);
        this.firebaseStorageManager = new FirebaseStorageManager();

        // sign in firebase user as anonymous user
        // this is needed for the get method
        final CountDownLatch latch = new CountDownLatch(1);
        auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                latch.countDown();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                latch.countDown();
            }
        });
        latch.await();

        // assert the user is not null
        assert auth.getCurrentUser() != null;
    }

    @Test
    public void testImage() {
        assert targetImage != null;
    }

    @Test
    public void testPutAndGetAndDeleteImage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        firebaseStorageManager.putImage(TESTFOLDER, TESTKEY, targetImage, new FirebaseStorageManager.Callback<String>() {
            @Override
            public void onComplete(String uri) {
                // assert image uploading successful
                assert !Objects.equals(uri, "");
                latch.countDown();
            }
        });

        latch.await();

        // test get method
        final CountDownLatch latch2 = new CountDownLatch(1);

        firebaseStorageManager.getImage(TESTFOLDER, TESTKEY, new FirebaseStorageManager.Callback<Uri>() {
            @Override
            public void onComplete(Uri result) {
                assert Objects.equals(result.getLastPathSegment(), TESTFOLDER + "/" + TESTKEY);  // comparing only the last path segment
                latch2.countDown();
            }
        });

        latch2.await();


        // test delete method
        final CountDownLatch latch3 = new CountDownLatch(1);

        firebaseStorageManager.deleteImage(TESTFOLDER, TESTKEY, new FirebaseStorageManager.Callback<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                assert result;      // delete successful
                latch3.countDown();
            }
        });

        latch3.await();

        final CountDownLatch latch4 = new CountDownLatch(1);
        firebaseStorageManager.getImage(TESTFOLDER, TESTKEY, new FirebaseStorageManager.Callback<Uri>() {
            @Override
            public void onComplete(Uri result) {
                assert result == null;
                latch4.countDown();
            }
        });

        latch4.await();
    }


}
