package com.example.lostandfound.FirebaseManagers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirestoreManager {

    private FirebaseFirestore db;

    public FirestoreManager(){
        db = FirebaseFirestore.getInstance();
    }

    // callback function to return result
    public interface Callback<T> {
        void onComplete(T result);
    }

    /**
     * Method to put new data to the database, or update data to put new value to database
     *
     * @param collection collection name
     * @param key key of items in the collection
     * @param value a map storing the new values
     * @param callback return true if successful, false otherwise
     */
    public void put(String collection, String key, Map<String, Object> value, Callback<Boolean> callback){
        db.collection(collection).document(key).set(value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onComplete(false);
            }
        });
    }

    /**
     * Method to put new data to the database, where the key is not specified and instead a unique ID is used for the key
     *
     * @param collection collection name
     * @param data  a map storing the new values
     * @param callback return the unique ID generated if successful, empty string otherwise
     */
    public void putWithUniqueId(String collection, Map<String, Object> data, Callback<String> callback){
        db.collection(collection).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String uid = documentReference.getId();
                callback.onComplete(uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onComplete("");
            }
        });
    }

    /**
     * Method to get data from the database when given the collection name and the key name
     *
     * @param collection collection name
     * @param key key of items in the collection
     * @param callback return the values in a map, or null if failed or values does not exist
     */
    public void get(String collection, String key, Callback<Map<String, Object>> callback){
        db.collection(collection).document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    callback.onComplete(documentSnapshot.getData());

                } else {
                    callback.onComplete(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onComplete(null);
            }
        });
    }


    /**
     * Deletes data from the firestore database when given a key
     *
     * @param collection collection name
     * @param key key of items in the collection
     * @param callback return true if successful, false otherwise. It returns successful even when the data does not exist
     */
    public void delete(String collection, String key, Callback<Boolean> callback){
        db.collection(collection).document(key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onComplete(false);
            }
        });
    }

    // method to check if user has internet connection
    /*
    Designed based on this post:
    https://stackoverflow.com/questions/50594146/firestore-timeout-for-android

    public boolean hasInternetConnection() {
        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

     */
}
