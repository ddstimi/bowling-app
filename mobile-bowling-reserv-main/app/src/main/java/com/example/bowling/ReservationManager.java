package com.example.bowling;

import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReservationManager {

    private static final String COLLECTION_NAME = "reservations";
    private final CollectionReference reservationsCollection;
    private static final String LOG_TAG = RegisterActivity.class.getName();

    public ReservationManager() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reservationsCollection = db.collection(COLLECTION_NAME);
    }

    public void saveReservation(String userId, String reservationTime, int numberOfPeople, int numberOfHours) {
        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("userId", userId);
        reservationData.put("reservationTime", reservationTime);
        reservationData.put("numberOfPeople", numberOfPeople);
        reservationData.put("numberOfHours", numberOfHours);

        reservationsCollection.add(reservationData)
                .addOnSuccessListener(documentReference -> Log.i(LOG_TAG, "Foglalás sikeresen mentve!"))
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba a foglalás mentésekor: " + e.getMessage()));
    }






    public void queryReservationsForUser(String userId, LinearLayout reservationsLayout) {
        String currentTime = getCurrentFormattedTime();

        Query query = reservationsCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThan("reservationTime", currentTime)
                .orderBy("reservationTime", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String reservationTime = document.getString("reservationTime");
                        Long numberOfPeople = document.getLong("numberOfPeople");
                        Long numberOfHours = document.getLong("numberOfHours");

                        if (reservationTime != null && numberOfPeople != null && numberOfHours != null) {
                            LinearLayout rowLayout = new LinearLayout(reservationsLayout.getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            rowLayout.setLayoutParams(params);
                            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                            TextView timeTextView = new TextView(reservationsLayout.getContext());
                            timeTextView.setText(reservationTime);
                            timeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1
                            ));
                            timeTextView.setTextSize(18);

                            TextView numberOfPeopleTextView = new TextView(reservationsLayout.getContext());
                            String numberOfPeopleText = String.valueOf(numberOfPeople);
                            numberOfPeopleTextView.setText(numberOfPeopleText);
                            LinearLayout.LayoutParams numberOfPeopleLayoutParams = new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1
                            );
                            numberOfPeopleLayoutParams.setMargins(40, 0, 0, 0);
                            numberOfPeopleTextView.setLayoutParams(numberOfPeopleLayoutParams);
                            numberOfPeopleTextView.setTextSize(18);

                            TextView numberOfHoursTextView = new TextView(reservationsLayout.getContext());
                            String numberOfHoursText = String.valueOf(numberOfHours);
                            numberOfHoursTextView.setText(numberOfHoursText);
                            LinearLayout.LayoutParams numberOfHoursLayoutParams = new LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1
                            );
                            numberOfHoursLayoutParams.setMargins(40, 0, 0, 0);
                            numberOfHoursTextView.setLayoutParams(numberOfHoursLayoutParams);
                            numberOfHoursTextView.setTextSize(18);

                            rowLayout.addView(timeTextView);
                            rowLayout.addView(numberOfPeopleTextView);
                            rowLayout.addView(numberOfHoursTextView);

                            reservationsLayout.addView(rowLayout);
                                } else {
                                    Log.e("ProfileActivity", "Nem megfelelő adatok a dokumentumban");
                                }
                            }
                            Log.d("ProfileActivity", "Foglalások lekérdezve");
                        } else {
                            Log.d("ProfileActivity", "Nincsenek foglalások a felhasználóhoz");
                        }
                    } else {
                        Log.w("ProfileActivity", "Hiba a foglalások lekérdezése során.", task.getException());
                    }
        });
    }


    private String getCurrentFormattedTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(new Date());
    }
}
