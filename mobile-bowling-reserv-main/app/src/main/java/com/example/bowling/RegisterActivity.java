package com.example.bowling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String LOG_TAG=RegisterActivity.class.getName();
    private static final String PREF_KEY= MainActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    EditText fullnameET;
    EditText usernameET;
    EditText phonenumberET;
    EditText birthdateET;
    EditText emailET;
    EditText passwordET;
    EditText passwordagainET;
    CheckBox acceptCB;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        fullnameET = findViewById(R.id.fullname_edittext);
        usernameET = findViewById(R.id.username_edittext);
        phonenumberET = findViewById(R.id.phone_edittext);
        birthdateET = findViewById(R.id.birthdate_edittext);
        emailET = findViewById(R.id.email_edittext);
        passwordET = findViewById(R.id.password_edittext);
        passwordagainET = findViewById(R.id.confirm_password_edittext);
        acceptCB = findViewById(R.id.accept_checkbox);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        emailET.setText(email);

        auth = FirebaseAuth.getInstance();
    }

    public void register(View view) {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordagain = passwordagainET.getText().toString();
        String fullname = fullnameET.getText().toString();
        String username = usernameET.getText().toString();
        String phone = phonenumberET.getText().toString();
        String birthdate = birthdateET.getText().toString();
        Boolean accept = acceptCB.isChecked();

        if (email.isEmpty()||password.isEmpty()||passwordagain.isEmpty()||fullname.isEmpty()||username.isEmpty()||phone.isEmpty()||birthdate.isEmpty()){
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length()<6){
            Toast.makeText(this, "Minimum 6 karakter hosszúnak kell lennie a jelszónak!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!password.equals(passwordagain)) {
            Toast.makeText(this, "Nem egyeznek a jelszavak!", Toast.LENGTH_SHORT).show();

            Log.e(LOG_TAG, "Nem egyeznek a jelszavak!");
            return;
        }

        if (!accept) {
            Toast.makeText(this, "Fogadd el a feltételeket!", Toast.LENGTH_SHORT).show();

            Log.e(LOG_TAG, "Fogadd el a feltételeket!");
            return;
        }

        // Firebase Authentication felhasználó létrehozása
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "User created");
                // Felhasználó sikeresen létrehozva, mentjük az adatait Firestore-ba
                saveUserData(fullname, username, phone, birthdate);
                Toast.makeText(this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                start();
            } else {
                Log.d(LOG_TAG, "Error while creating user");
            }
        });
    }

    private void saveUserData(String fullName, String username, String phone, String birthdate) {
        // FirebaseUser objektum lekérése a bejelentkezett felhasználóról
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Felhasználó egyedi azonosítójának lekérése
            String uid = user.getUid();

            // Firestore adatbázis hivatkozásának létrehozása
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Felhasználó adatainak tárolása a "users" kollekcióban
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", user.getEmail());
            userData.put("phoneNumber", phone);
            userData.put("fullName", fullName);
            userData.put("username", username);
            userData.put("birthdate", birthdate);

            // "users" kollekcióhoz tartozó dokumentum létrehozása az egyedi azonosítóval (UID)
            db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "Felhasználó adatai sikeresen mentve a Firestore adatbázisba"))
                    .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a felhasználó adatainak mentése közben", e));
        } else {
            Log.e(LOG_TAG, "Felhasználó nincs bejelentkezve");
        }
    }

    public void start() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}