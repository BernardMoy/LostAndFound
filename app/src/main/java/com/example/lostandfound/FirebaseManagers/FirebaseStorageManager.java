package com.example.lostandfound.FirebaseManagers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageManager {
    private final FirebaseStorage storage;
    private final StorageReference storageReference;

    // callback function to return result
    public interface Callback<T> {
        void onComplete(T result);
    }

    public FirebaseStorageManager() {
        storage = FirebaseStorage.getInstance(); // this must be the folder path
        storageReference = storage.getReference();
    }

    /**
     * Put image given a key and a uri.
     * The image will be stored as "folder/key.jpg"
     * return the DOWNLOAD URL (NOT THE UPLOAD URL), or empty string if fails
     *
     * @param folder   directory where the image is stored
     * @param key      unique identifier of the image, also name of the image
     * @param image    Uri image from user uploads
     * @param callback Return true if successful, false otherwise
     */
    public void putImage(String folder, String key, Uri image, Callback<String> callback) {
        // if image is null, return false
        if (image == null) {
            callback.onComplete("");
            return;
        }

        StorageReference imageReference = storageReference.child(folder).child(key);
        imageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get the DOWNLOAD URL of the uploaded image
                // so that it can be accessed using the getImage method
                // without having to refer to the full path again
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        callback.onComplete(uri.toString());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("STORAGE ERROR", e.getMessage());
                        callback.onComplete("");

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("STORAGE ERROR", e.getMessage());
                callback.onComplete("");
            }
        });
    }

    /**
     * Given a folder and key, return the Uri
     *
     * @param folder   directory where the image is stored
     * @param key      unique identifier of the image, also name of the image
     * @param callback Uri of the image, or null if not found
     */
    public void getImage(String folder, String key, Callback<Uri> callback) {
        StorageReference imageReference = storageReference.child(folder).child(key);
        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // return the uri
                callback.onComplete(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // return null
                callback.onComplete(null);
            }
        });
    }

    /**
     * Given a folder and a key, delete the image with the name as the key
     *
     * @param folder   directory where the image is stored
     * @param key      unique identifier of the image, also name of the image
     * @param callback true if operation successful (Even when entry does not exist), false otherwise
     */
    public void deleteImage(String folder, String key, Callback<Boolean> callback) {
        StorageReference imageReference = storageReference.child(folder).child(key);
        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onComplete(true);   // delete successful, whether or not the image exists
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onComplete(false);
            }
        });
    }
}
