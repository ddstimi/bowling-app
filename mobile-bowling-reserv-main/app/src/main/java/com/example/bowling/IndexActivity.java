package com.example.bowling;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class IndexActivity extends AppCompatActivity {

    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final int PERMISSION_REQUEST_CALL = 1;

    private ReservationNotificationManager mNotificationHandler;
    ReservationManager reservationManager;

    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_index);

        user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Log.e(LOG_TAG,"Authenticated user!");
        }else{
            Log.e(LOG_TAG,"Unauthenticated!");
            finish();
        }
        reservationManager= new ReservationManager();

        ImageView logo = findViewById(R.id.logo_image);
        TextView bowling = findViewById(R.id.bowling_text);
        LinearLayout menu = findViewById(R.id.menu_bar);
        CardView intro = findViewById(R.id.intro_card);
        CardView open = findViewById(R.id.open_card);
        CardView help=findViewById(R.id.help_card);

        // Animáció betöltése
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_page);

        // Animáció hozzáadása a TextView elemekhez
        logo.startAnimation(fadeInAnimation);
        bowling.startAnimation(fadeInAnimation);
        menu.startAnimation(fadeInAnimation);
        intro.startAnimation(fadeInAnimation);
        open.startAnimation(fadeInAnimation);
        help.startAnimation(fadeInAnimation);

        checkReservationWithin30Minutes();

        mNotificationHandler = new ReservationNotificationManager(this);
        mNotificationHandler.send("Halo");
    }

    private void checkReservationWithin30Minutes() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30); // Hozzáadunk 30 percet az aktuális időhöz

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String futureTime = dateFormat.format(calendar.getTime());

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

    public void indexPage(View view) {
        Intent intent = new Intent(this, IndexActivity.class);
        intent.putExtra("SECRET_KEY",99);
        intent.setDataAndType(Uri.parse("file://" + "/path/to/your/activity_index.xml"), "text/xml");
        startActivity(intent);
    }

    public void contactSupport(View view) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeInAnimation);
        // Ellenőrizd, hogy van-e engedély a hívás indításához
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Ha nincs engedély, kérj engedélyt
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL);
        } else {
            // Ha van engedély, indítsd el a hívást
            startCall();
        }
    }

    private void startCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+0")); // Ide írd be az ügyfélszolgálat telefonszámát
        startActivity(callIntent);
    }

    // Engedélykérés eredményének kezelése
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ha a felhasználó engedélyezte a hívást, indítsd el a hívást
                startCall();
            } else {
                // Ha a felhasználó elutasította az engedélyt, adj neki visszajelzést
                Toast.makeText(this, "A híváshoz való hozzáférés megtagadva", Toast.LENGTH_SHORT).show();
            }
        }
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