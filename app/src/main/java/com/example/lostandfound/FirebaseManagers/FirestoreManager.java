package com.example.lostandfound.FirebaseManagers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 Contains utility firebase firestore methods
 Provides extra functionality such as putting error messages to logs

 */
public class FirestoreManager {

    private final FirebaseFirestore db;

    public FirestoreManager() {
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
     * @param key        key of items in the collection
     * @param value      a map storing the new values
     * @param callback   return true if successful, false otherwise
     */
    public void put(String collection, String key, Map<String, Object> value, Callback<Boolean> callback) {
        db.collection(collection).document(key).set(value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                callback.onComplete(false);
            }
        });
    }

    /**
     * Method to put new data to the database, where the key is not specified and instead a unique ID is used for the key
     *
     * @param collection collection name
     * @param data       a map storing the new values
     * @param callback   return the unique ID generated if successful, empty string otherwise
     */
    public void putWithUniqueId(String collection, Map<String, Object> data, Callback<String> callback) {
        db.collection(collection).add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String uid = documentReference.getId();
                callback.onComplete(uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                callback.onComplete("");
            }
        });
    }

    /**
     * Method to get data from the database when given the collection name and the key name
     *
     * @param collection collection name
     * @param key        key of items in the collection
     * @param callback   return the values in a map, or null if failed or values does not exist
     *                   it should only return a single key value pair as the key for the collection is unique.
     */
    public void get(String collection, String key, Callback<Map<String, Object>> callback) {
        db.collection(collection).document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    callback.onComplete(documentSnapshot.getData());

                } else {
                    callback.onComplete(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                callback.onComplete(null);
            }
        });
    }

    /**
     * Method to get data from the database,
     * given attribute and the value it equals to and also specify a key to order the data by.
     *
     * @param collection collection name
     * @param attribute  the attribute of the value
     * @param whereValue the value that the attribute should equal to
     * @param callback   return the list of document ids, or null if failed or values does not exist
     */
    public void getIdsWhere(String collection,
                            String attribute,
                            Object whereValue,
                            String orderByKey,
                            Callback<List<String>> callback) {
        db.collection(collection)
                .whereEqualTo(attribute, whereValue)
                .orderBy(orderByKey, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> result = new ArrayList<>();

                        // for each matching data, get it and add it to result
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            result.add(snapshot.getId());
                        }

                        // return the result
                        callback.onComplete(result);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // return null
                        Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                        callback.onComplete(null);
                    }
                });
    }


    /**
     * Method to get data from the database,
     * returning all the keys (item ids) regardless of condition
     *
     * @param collection collection name
     * @param callback   return the list of document ids, or null if failed or collection doesnt exist
     */
    public void getAllIds(String collection, Callback<List<String>> callback) {
        db.collection(collection).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> result = new ArrayList<>();

                // for each matching data, get it and add it to result
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    result.add(snapshot.getId());
                }

                // return the result
                callback.onComplete(result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // return null
                Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                callback.onComplete(null);
            }
        });
    }


    /**
     * Deletes data from the firestore database when given a key
     *
     * @param collection collection name
     * @param key        key of items in the collection
     * @param callback   return true if successful, false otherwise. It returns successful even when the data does not exist
     */
    public void delete(String collection, String key, Callback<Boolean> callback) {
        db.collection(collection).document(key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                callback.onComplete(false);
            }
        });
    }

    // method to check if user has internet connection - to be implemented
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

    /**
     * Given a collection, a key, an attribute name and a new value,
     * update the attribute value to the new value.
     *
     * @param collection Collection
     * @param key        key / itemID
     * @param attribute  attribute name
     * @param newValue   new value of the attribute after updating
     * @param callback   return true if successful, false otherwise
     */
    public void update(String collection, String key, String attribute, Object newValue, Callback<Boolean> callback) {
        db.collection(collection).document(key)
                .update(attribute, newValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onComplete(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FIREBASE MANAGER ERROR", e.getMessage());
                        callback.onComplete(false);
                    }
                });
    }
}
