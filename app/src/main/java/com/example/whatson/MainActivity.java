package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    FirebaseAuth firebaseAuth;

    String username = null, password = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("loginaccount", 0);

        username = sharedPreferences.getString("currentuserusername", "");
        password = sharedPreferences.getString("currentuserpassword", "");


        if(isNetworkAvailable(MainActivity.this)) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (username.equals("") || password.equals("")) {
                        Intent intent = new Intent(MainActivity.this, loginactivity.class);
                        overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
                        startActivity(intent);
                        finish();
                    } else {

                        /// login drirectly
                        firebaseAuth.signInWithEmailAndPassword(username, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            Intent intent = new Intent(MainActivity.this, userprofile.class);
                                            overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                                            intent.putExtra("uniqueidofcurrentuser", firebaseAuth.getUid());
                                            startActivity(intent);
                                            finish();

                                        } else {

                                            Intent intent = new Intent(MainActivity.this, loginactivity.class);
                                            overridePendingTransition(R.anim.zoomout, R.anim.zoomin);
                                            startActivity(intent);
                                            finish();

                                        }


                                    }
                                });

                    }
                }
            }, 1200);

        } else {
            Toast.makeText(MainActivity.this, "No Internet\nConnect and Restart.", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
