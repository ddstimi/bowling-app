package com.example.bowling;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationNotificationManager {

    private Context context;
    private static final String CHANNEL_ID = "foglalasok_channel";
    private static final String CHANNEL_NAME = "Foglalások";
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    public ReservationNotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = "Értesítések a közelgő foglalásokról";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification(String reservationTime) {
        try {
            Date reservationDate = parseTimeSlot(reservationTime);
            long reservationTimeInMillis = reservationDate.getTime();
            long currentTime = System.currentTimeMillis();
            long timeDifference = reservationTimeInMillis - currentTime;

            if (timeDifference > 0 && timeDifference <= 30 * 60 * 1000) { // Fél óra vagy kevesebb van hátra a foglalási időpontig
                showNotification();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Date parseTimeSlot(String timeSlot) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.parse(timeSlot);
    }

    public void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // helyettesítő ikon
                .setContentTitle("Foglalás emlékeztető")
                .setContentText("Önnek fél óra múlva van egy foglalása!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Hiányzó jogosultságok kérése
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        } else {
            // Jogosultság rendelkezésre áll, értesítés kiküldése
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
