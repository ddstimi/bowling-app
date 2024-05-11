package com.example.bowling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG=MainActivity.class.getName();
    private static final String PREF_KEY=MainActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private static final int SECRET_KEY=99;
    EditText emailET;
    EditText passwordET;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        emailET=findViewById(R.id.email_edittext);
        passwordET=findViewById(R.id.password_edittext);

        auth=FirebaseAuth.getInstance();

        preferences=getSharedPreferences(PREF_KEY,MODE_PRIVATE);
    }

    public void login(View view) {


        String email=emailET.getText().toString();
        String password=passwordET.getText().toString();

        if(email.isEmpty()||password.isEmpty()){
            Toast.makeText(this, "Minden mezőt ki kell tölteni!", Toast.LENGTH_SHORT).show();
            return;
        }else {
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(LOG_TAG,"Successfully signing in user");
                        start();
                    }else{
                        Toast.makeText(MainActivity.this, "Nem megfelelő felhasználónév vagy jelszó", Toast.LENGTH_SHORT).show();
                        Log.d(LOG_TAG,"Error while signing in user");
                    }
                }
            });
        }

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeInAnimation);



    }
    public void start() {
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
    }

    public void registerPage(View view) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeInAnimation);

        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY",99);
        intent.setDataAndType(Uri.parse("file://" + "/path/to/your/activity_register.xml"), "text/xml");
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("email",emailET.getText().toString());
        editor.putString("password",passwordET.getText().toString());
        editor.apply();
    }



}