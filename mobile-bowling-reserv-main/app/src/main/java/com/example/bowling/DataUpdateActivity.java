package com.example.bowling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataUpdateActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private Button buttonSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data_update);

        ImageView logo = findViewById(R.id.logo_image);
        TextView bowling = findViewById(R.id.bowling_text);
        LinearLayout menu = findViewById(R.id.menu_bar);
        CardView profile = findViewById(R.id.profile_card);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_page);

        logo.startAnimation(fadeInAnimation);
        bowling.startAnimation(fadeInAnimation);
        menu.startAnimation(fadeInAnimation);
        profile.startAnimation(fadeInAnimation);


        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSave = findViewById(R.id.update_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            editTextName.setText(user.getDisplayName());
            editTextEmail.setText(user.getEmail());

        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();

            }
        });
    }

    private void saveChanges() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String newEmail = editTextEmail.getText().toString().trim();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(editTextName.getText().toString().trim())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateUserInDatabase(user.getUid(), editTextName.getText().toString().trim(), newEmail);
                        } else {
                            Toast.makeText(DataUpdateActivity.this, "Sikertelen frissítés", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void updateUserInDatabase(String userId, String newName, String NewEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("fullName", newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DataUpdateActivity.this, "Teljes név sikeresen frissítve", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DataUpdateActivity.this, "Sikertelen teljes név frissítés az adatbázisban", Toast.LENGTH_SHORT).show();
                });
        userRef.update("email", NewEmail)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DataUpdateActivity.this, "E-mail cím sikeresen frissítve", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DataUpdateActivity.this, "Sikertelen e-mail cím frissítés az adatbázisban", Toast.LENGTH_SHORT).show();
                });

    }



    public void indexPage(View view) {
        Intent intent = new Intent(this, IndexActivity.class);
        intent.putExtra("SECRET_KEY",99);
        intent.setDataAndType(Uri.parse("file://" + "/path/to/your/activity_index.xml"), "text/xml");
        startActivity(intent);
    }

    public void reservPage(View view) {
        Intent intent = new Intent(this, ReservActivity.class);
        intent.putExtra("SECRET_KEY",99);
        intent.setDataAndType(Uri.parse("file://" + "/path/to/your/activity_reserv.xml"), "text/xml");
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("SECRET_KEY",99);
        intent.setDataAndType(Uri.parse("file://" + "/path/to/your/activity_profile.xml"), "text/xml");
        startActivity(intent);
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}