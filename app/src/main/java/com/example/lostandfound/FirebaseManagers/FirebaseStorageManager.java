package com.example.lostandfound.FirebaseManagers;

import com.google.firebase.storage.FirebaseStorage;

public class FirebaseStorageManager {
    private FirebaseStorage storage;

    public FirebaseStorageManager(){
        storage = FirebaseStorage.getInstance();
    }
}
