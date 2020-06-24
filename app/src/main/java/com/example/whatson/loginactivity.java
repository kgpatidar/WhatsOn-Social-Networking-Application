package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginactivity extends AppCompatActivity {

    EditText usernameText, passwordText;
    CheckBox rememberMeCheck;
    Button login;
    TextView signup , orText;
    ImageView logo;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        // adding to interface
        logo = findViewById(R.id.logooflogin);
        usernameText = findViewById(R.id.usernameloginpage);
        passwordText = findViewById(R.id.passwordloginpage);
        login = findViewById(R.id.loginbutton);
        signup = findViewById(R.id.signuptextview);
        rememberMeCheck = findViewById(R.id.remembermelogin);
        progressBar = findViewById(R.id.loginprogress);
        orText = findViewById(R.id.ortextview);
        //****************************************************************

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("loginaccount", 0);

        //************* signup intent
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginactivity.this, signupactivity.class);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                intent.putExtra("keyofcuurentuser", "");
                startActivity(intent);
                finish();
            }
        });




        //*********** login intent
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login.setEnabled(false);
                signup.setVisibility(View.INVISIBLE);
                orText.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                login.setText("FETCHING...");
                progressBar.setProgress(30);

                final String username = usernameText.getText().toString() + "@gmail.com";
                final String password = passwordText.getText().toString();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

                    firebaseAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        // setting up account remeber
                                        if(rememberMeCheck.isChecked()) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("currentuserusername", username);
                                            editor.putString("currentuserpassword", password);
                                            editor.commit();
                                        }

                                        Intent intent = new Intent(loginactivity.this, userprofile.class);
                                        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                                        intent.putExtra("uniqueidofcurrentuser", firebaseAuth.getUid());
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        login.setEnabled(true);
                                        signup.setVisibility(View.VISIBLE);
                                        orText.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        login.setText("LOGIN");
                                        Toast.makeText(loginactivity.this, "No Matched!", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                } else {
                    login.setEnabled(true);
                    signup.setVisibility(View.VISIBLE);
                    orText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setText("LOGIN");
                    Toast.makeText(loginactivity.this, "Field Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}








