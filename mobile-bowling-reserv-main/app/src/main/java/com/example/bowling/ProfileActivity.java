package com.example.bowling;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private ReservationManager reservationManager;
    private LinearLayout reservationsLayout;
    private UserProfileManager userProfileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        Bundle bundle = getIntent().getExtras();
        int secret_key= bundle.getInt("SECRET_KEY");
        if(secret_key!=99){
            finish();
        }
        reservationsLayout = findViewById(R.id.reservationsLayout);
        reservationManager = new ReservationManager();
        userProfileManager = new UserProfileManager();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            if (userId != null && !userId.isEmpty()) {
                userProfileManager.fetchUserProfileData(userId, findViewById(R.id.userDataLayout));
                reservationManager.queryReservationsForUser(userId, reservationsLayout);
                Log.d("ProfileActivity", userId);
            } else {
                Log.e("ProfileActivity", "Hibás felhasználó azonosító.");
            }
        } else {
            Log.e("ProfileActivity", "Nincs bejelentkezve felhasználó.");
        }

        ImageView logo = findViewById(R.id.logo_image);
        TextView bowling = findViewById(R.id.bowling_text);
        LinearLayout menu = findViewById(R.id.menu_bar);
        CardView profile = findViewById(R.id.profile_card);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_page);

        logo.startAnimation(fadeInAnimation);
        bowling.startAnimation(fadeInAnimation);
        menu.startAnimation(fadeInAnimation);
        profile.startAnimation(fadeInAnimation);
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

    public void deleteProfile(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profil törlése");
        builder.setMessage("Biztosan törölni szeretné a profilját? Ez véglegesen eltávolítja az összes adatát az alkalmazásból.");

        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProfile();
            }
        });

        builder.setNegativeButton("Nem", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Profil sikeresen törölve", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            startActivity(intent);                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Profil törlése sikertelen", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void dataUpdate(View view) {
        Intent intent = new Intent(this, DataUpdateActivity.class);
        intent.putExtra("SECRET_KEY",99);
        intent.setDataAndType(Uri.parse("file://" + "/path/to/your/activity_data_update.xml"), "text/xml");
        startActivity(intent);
    }
}