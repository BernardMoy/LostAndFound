package com.example.lostandfound.FirebaseManagers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageManager {
    private FirebaseStorage storage;
    private StorageReference storageReference;

    // callback function to return result
    public interface Callback<T> {
        void onComplete(T result);
    }

    public FirebaseStorageManager(){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    /**
     * Put image given a key and a uri.
     *
     * @param key Unique id, can be the id associated with an item
     * @param image Uri image from user uploads
     * @param callback Return true if successful, false otherwise
     */
    public void putImage(String key, Uri image, Callback<Boolean> callback){
        // if image is null, return
        if (image == null){
            return;
        }

        // create reference of the image as a path
        StorageReference imageReference = storageReference.child("images/" + key + ".jpg");
        Log.d("REF", imageReference.getPath());

        // put the file to the path
        imageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onComplete(false);
                Log.d("ERROR", e.getMessage());
            }
        });
    }

    public void getImage(String key, Callback<Uri> callback){

    }
}
