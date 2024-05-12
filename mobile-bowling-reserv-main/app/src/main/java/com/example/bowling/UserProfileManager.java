package com.example.bowling;


import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileManager {

    private static final String TAG = "UserProfileManager";
    private final FirebaseFirestore db;

    public UserProfileManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void fetchUserProfileData(String userId, ConstraintLayout userDataLayout) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("fullName");
                                String email = document.getString("email");
                                String phone = document.getString("phoneNumber");

                                TextView nameTextView = userDataLayout.findViewById(R.id.name_textview);
                                nameTextView.setText("Név: " + name);

                                TextView emailTextView = userDataLayout.findViewById(R.id.email_textview);
                                emailTextView.setText("Email cím: " + email);

                                TextView phoneTextView = userDataLayout.findViewById(R.id.phone_textview);
                                phoneTextView.setText("Telefonszám: " + phone);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }
}

