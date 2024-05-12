package com.example.bowling;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservActivity extends AppCompatActivity {

    private static final String LOG_TAG=ReservActivity.class.getName();
    private ReservationManager reservationManager;
    private ReservationNotificationManager mNotificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserv);
        Bundle bundle = getIntent().getExtras();
        int secret_key= bundle.getInt("SECRET_KEY");
        if(secret_key!=99){
            finish();
        }

        Spinner spinner = findViewById(R.id.date_spinner);

        List<String> options = generateTimeSlotsForAWeek();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        reservationManager = new ReservationManager();

        ImageView logo = findViewById(R.id.logo_image);
        TextView bowling = findViewById(R.id.bowling_text);
        LinearLayout menu = findViewById(R.id.menu_bar);
        CardView reserv = findViewById(R.id.reservation_card);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_page);

        logo.startAnimation(fadeInAnimation);
        bowling.startAnimation(fadeInAnimation);
        menu.startAnimation(fadeInAnimation);
        reserv.startAnimation(fadeInAnimation);
    }

    private List<String> generateTimeSlotsForAWeek() {
        List<String> timeSlots = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        for (int i = 0; i < 7; i++) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentDate);

            currentCalendar.add(Calendar.DATE, i);

            int startingHour = 10;
            if (i == 0) {
                startingHour=currentCalendar.get(Calendar.HOUR_OF_DAY)+1;
            }

            if (isWeekday(currentCalendar)) {
                generateTimeSlotsForDay(currentCalendar, startingHour, 19, timeSlots);
            } else if (isFridayOrSaturday(currentCalendar)) {
                generateTimeSlotsForDay(currentCalendar, startingHour, 23, timeSlots);
            }
        }

        return timeSlots;
    }

    private void generateTimeSlotsForDay(Calendar calendar, int startingHour, int closingHour, List<String> timeSlots) {
        int openingHour = startingHour;

        for (int j = openingHour; j <= closingHour; j++) {
            calendar.set(Calendar.HOUR_OF_DAY, j);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedTime = dateFormat.format(calendar.getTime());

            timeSlots.add(formattedTime);
        }
    }




    private boolean isWeekday(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.THURSDAY;
    }


    private boolean isFridayOrSaturday(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY;
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

    public void reserv(View view) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeInAnimation);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userId = null;

        if (user != null) {
            userId = user.getUid();
        }

        EditText peopleEditText = findViewById(R.id.people_edittext);
        Spinner dateSpinner = findViewById(R.id.date_spinner);
        EditText hourEditText = findViewById(R.id.hour_edittext);

        String people = peopleEditText.getText().toString();
        String reservationTime = dateSpinner.getSelectedItem().toString();
        String reservationHour = hourEditText.getText().toString();

        if (people.isEmpty() || reservationHour.isEmpty()) {
            Log.e(LOG_TAG, "Mindent kötelező kitölteni!");
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfPeople = Integer.parseInt(people);
        int numberOfHours = Integer.parseInt(reservationHour);


        CollectionReference reservationsRef = FirebaseFirestore.getInstance().collection("reservations");

        Query query = reservationsRef
                .whereGreaterThanOrEqualTo("reservationTime", reservationTime)
                .orderBy("reservationTime")
                .limit(1);

        final String finalId=userId;

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    int totalReservedPlaces = document.getLong("numberOfPeople").intValue();

                    if ((totalReservedPlaces + numberOfPeople) > 20 || totalReservedPlaces >= 20 || numberOfPeople > 20) {
                        Toast.makeText(this, "Sajnos nincs elég szabad hely a kiválasztott időpontban! Válasszon későbbi időpontot!", Toast.LENGTH_SHORT).show();
                        Log.d("ReservationCheck", "Nincs elég szabad hely a foglaláshoz az adott időpontban és időtartamban.");
                    } else {

                        reservationManager.saveReservation(finalId, reservationTime, numberOfPeople, numberOfHours);
                        mNotificationHandler = new ReservationNotificationManager(this);
                        mNotificationHandler.send("Sikeres foglalás! Ne felejtsen el a kiválasztott időpont előtt 30 perccel megjelenni!\n Köszönjük, hogy minket választott");
                        Log.i(LOG_TAG, "Foglalás sikeresen mentve!");
                        peopleEditText.setText("");
                        hourEditText.setText("");
                    }
                } else {
                    reservationManager.saveReservation(finalId, reservationTime, numberOfPeople, numberOfHours);
                    mNotificationHandler = new ReservationNotificationManager(this);
                    mNotificationHandler.send("Sikeres foglalás! Ne felejtsen el a kiválasztott időpont előtt 30 perccel megjelenni!\n Köszönjük, hogy minket választott");
                    Log.i(LOG_TAG, "Foglalás sikeresen mentve!");
                    peopleEditText.setText("");
                    hourEditText.setText("");
                }
            } else {
                Log.e("ReservationCheck", "Hiba történt a lekérdezés során.", task.getException());
            }
        });
    }


}