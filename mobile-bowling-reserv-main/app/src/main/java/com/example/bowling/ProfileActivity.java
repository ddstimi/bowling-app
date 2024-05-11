package com.example.bowling;

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


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
        reservationsLayout = findViewById(R.id.reservationsLayout); // Hozzárendeljük a layoutot a változóhoz

        reservationManager = new ReservationManager();
        userProfileManager = new UserProfileManager(); // létrehozzuk a userProfileManager példányt

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            if (userId != null && !userId.isEmpty()) {
                // Helyes felhasználó azonosító átadása a lekérdezésnek
                userProfileManager.fetchUserProfileData(userId, findViewById(R.id.userDataLayout)); // meghívjuk a fetchUserProfileData metódust
                reservationManager.queryReservationsForUser(userId, reservationsLayout);
                Log.d("ProfileActivity", userId);
            } else {
                // Hibás azonosító esetén hibakezelés
                Log.e("ProfileActivity", "Hibás felhasználó azonosító.");
            }
        } else {
            // Ha a felhasználó null, akkor nincs bejelentkezve
            Log.e("ProfileActivity", "Nincs bejelentkezve felhasználó.");
        }

        ImageView logo = findViewById(R.id.logo_image);
        TextView bowling = findViewById(R.id.bowling_text);
        LinearLayout menu = findViewById(R.id.menu_bar);
        CardView profile = findViewById(R.id.profile_card);
        // Animáció betöltése
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_page);

        // Animáció hozzáadása a TextView elemekhez
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

        // Visszairányítjuk a felhasználót a bejelentkezési oldalra
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Törli az összes előző Activity-t a visszairányítás előtt
        startActivity(intent);
        finish();
    }
}