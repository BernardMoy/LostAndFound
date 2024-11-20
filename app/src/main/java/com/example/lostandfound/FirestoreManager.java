package com.example.lostandfound;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class FirestoreManager {

    private Context ctx;
    private FirebaseFirestore db;

    public FirestoreManager(Context ctx){
        this.ctx = ctx;
        db = FirebaseFirestore.getInstance();
    }

    // callback function to return result
    public interface Callback<T> {
        void onComplete(T result);
    }

    // method to put a collection, key, value pair into the db

    /**
     * Method to put new data to the database
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
                Toast.makeText(ctx, "Error adding information to database", Toast.LENGTH_SHORT).show();
                callback.onComplete(false);
            }
        });
    }

    // method to return a map of values, given a collection and a key, or null if the collection or key does not exist
    // null values need to be checked before casting

    /**
     * Method to get data from the database when given the collection name and the key name
     *
     * @param collection collection name
     * @param key key of items in the collection
     * @param callback return the values in a map, or null if failed (Need to check)
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
                Toast.makeText(ctx, "Error fetching information from database", Toast.LENGTH_SHORT).show();
                callback.onComplete(null);
            }
        });
    }


    /**
     * Updates data from the firestore database
     *
     * @param collection collection name
     * @param key key of items in the collection
     * @param newValue a map storing the new values
     * @param callback return true if successful, false otherwise
     */
    public void update(String collection, String key, Map<String, Object> newValue, Callback<Boolean> callback){
        db.collection(collection).document(key).update(newValue).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx, "Error updating information to database", Toast.LENGTH_SHORT).show();
                callback.onComplete(false);
            }
        });
    }
}
