package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.UnicodeSetSpanner;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.picker.MaterialDatePickerDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class signupactivity extends AppCompatActivity {

    // intializing UI
    ImageView profileImg;
    EditText name, username, password, date, month, year;
    RadioButton male, female;
    TextView loginIntent;
    Button signup;
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;

    String userUniqueId = "";
    String profileImageUri = "profile.jpeg";
    boolean isFromEdit = false;

    // Image Upload***
    Uri imageUri = null;


    //others
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> usernamelisttocheck;
    StorageReference storageReference;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);

        // *************** interaction with user interface ****************************
        profileImg = findViewById(R.id.profilepictureimageviewsignup);
        name = findViewById(R.id.fullnamesignup);
        username = findViewById(R.id.usernamesignup);
        password = findViewById(R.id.passwordsignup);
        date = findViewById(R.id.dateEditText);
        month = findViewById(R.id.monthEditText);
        year = findViewById(R.id.yearEdittext);
        male = findViewById(R.id.radiomalesignup);
        female = findViewById(R.id.radiofemalesignup);
        loginIntent = findViewById(R.id.intenttoreachlogin);
        signup = findViewById(R.id.signupbuttoninsignupmode);
        progressBar = findViewById(R.id.signuprogress);
        progressBar.setVisibility(View.INVISIBLE);
        bottomNavigationView = findViewById(R.id.navigatorprofile);
        Picasso.get().load(imageUri).into(profileImg);
        profileImg.setImageResource(R.drawable.defaultprofile);

        imageUri = Uri.parse("android.resource://com.example.whatson/" + R.drawable.defaultprofile);

        //****************************************************************************

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("profilepicture");

        //****************************************************************************

        usernamelisttocheck = new ArrayList<String>();

        //****************************************************************************

        //******************** Getting intent from edit side *************************


        // ****************  Setting fields from data *******************************


        //*********signup to login*************************************************//
        loginIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signupactivity.this, loginactivity.class);
                overridePendingTransition(R.anim.zoomout, R.anim.zoomout);
                startActivity(intent);
                finish();
            }
        });
        //***************************************************************************

        //**** Accesing username in array list *************************************

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernamelisttocheck.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User usernameUserObject = snap.getValue(User.class);
                    usernamelisttocheck.add(usernameUserObject.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //******************On clicking signup Button**********************************
        signup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                progressBar.setMax(100);
                progressBar.setMin(20);
                signup.setEnabled(false);
                signup.setText("Uploading");
                progressBar.setVisibility(View.VISIBLE);
                loginIntent.setVisibility(View.INVISIBLE);
                bottomNavigationView.setVisibility(View.INVISIBLE);
                progressBar.setProgress(30);
                //******* Getting UI data into variables
                final String fullName = capitalizeWord(name.getText().toString());
                final String usernameText = username.getText().toString().trim() + "@gmail.com";
                final String passwordText = password.getText().toString().trim();
                String DATE = date.getText().toString().trim();
                String MONTH = month.getText().toString().trim();
                String YEAR = year.getText().toString().trim();


                if (isDateFormetted(DATE, MONTH, YEAR)) {
                    final String DateOfBirth = DATE + "/" + MONTH  + "/" + YEAR;
                    final String imageUrl = "abcd.jpeg";
                    String gender = "";
                    if (male.isChecked()) {
                        gender = "Male";
                    } else if (female.isChecked()) {
                        gender = "Female";
                    }
                    //********* Checking empty fields
                    if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(usernameText) &&
                            !TextUtils.isEmpty(passwordText) && !date.getText().toString().equals("") &&
                            !month.getText().toString().equals("") && !year.getText().toString().equals("")) {

                        if (Integer.parseInt(date.getText().toString()) < 32 && Integer.parseInt(month.getText().toString()) < 13 &&
                                Integer.parseInt(year.getText().toString()) < 2020) {

                            String[] nameSplit = fullName.split(" ");

                            if (nameSplit.length > 1) {

                                if (usernameStrong(usernameText)) {
                                    if (!usernameExistace(usernameText)) {
                                        // ******** all ready performance to set data into firebase****************************
                                        final String finalGender = gender;
                                        firebaseAuth.createUserWithEmailAndPassword(usernameText, passwordText)
                                                .addOnCompleteListener(signupactivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {

                                                            final String key = firebaseAuth.getUid();
                                                            //******************************************************************************
                                                            //********************  Uploading Profile Picture ******************************
                                                            //******************************************************************************

                                                            if(profileImg != null) {

                                                                final StorageReference fileRefrence = storageReference.child(key +
                                                                        "." + getFileExtension(imageUri));

                                                                fileRefrence.putFile(imageUri)
                                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                                profileImageUri = taskSnapshot.getUploadSessionUri().toString();
                                                                                //***********************************************************************
                                                                                fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                    @Override
                                                                                    public void onSuccess(Uri uri) {

                                                                                        profileImageUri = uri.toString();
                                                                                        User user = new User(key, fullName, usernameText, passwordText, profileImageUri, DateOfBirth, finalGender, "0");
                                                                                        databaseReference.child(key).setValue(user);

                                                                                        Intent intent = new Intent(signupactivity.this, loginactivity.class);
                                                                                        startActivity(intent);
                                                                                        finish();
                                                                                    }
                                                                                });

                                                                                //************************************************************************



                                                                                // Toast.makeText(signupactivity.this, "Setting your Account", Toast.LENGTH_SHORT).show();


    //                                                                            Toast.makeText(signupactivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                signup.setText("Almost Ready...");
    //                                                                            Toast.makeText(signupactivity.this, "Almost Ready!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnCanceledListener(new OnCanceledListener() {
                                                                            @Override
                                                                            public void onCanceled() {
                                                                                Toast.makeText(signupactivity.this, "Image Fail!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(signupactivity.this, "No Selected File", Toast.LENGTH_SHORT).show();
                                                                signup.setEnabled(true);
                                                                signup.setText("SIGNUP");
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                loginIntent.setVisibility(View.VISIBLE);
                                                                bottomNavigationView.setVisibility(View.VISIBLE);

                                                            }






                                                        } else {
                                                            Toast.makeText(signupactivity.this, "All Not Ready", Toast.LENGTH_SHORT).show();
                                                            signup.setEnabled(true);
                                                            signup.setText("SIGNUP");
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            loginIntent.setVisibility(View.VISIBLE);
                                                            bottomNavigationView.setVisibility(View.VISIBLE);
                                                        }
                                                    }
                                                });

                                    } else {
                                        signup.setEnabled(true);
                                        signup.setText("SIGNUP");
                                        progressBar.setVisibility(View.INVISIBLE);
                                        loginIntent.setVisibility(View.VISIBLE);
                                        bottomNavigationView.setVisibility(View.VISIBLE);
                                        Toast.makeText(signupactivity.this, "Username Already exist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(signupactivity.this, "Username Must contain Character and Digit", Toast.LENGTH_SHORT).show();
                                    signup.setEnabled(true);
                                    signup.setText("SIGNUP");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    loginIntent.setVisibility(View.VISIBLE);
                                    bottomNavigationView.setVisibility(View.VISIBLE);
                                }


                                //*************************************************************************************

                            } else {
                                Toast.makeText(signupactivity.this, "Enter full Name", Toast.LENGTH_SHORT).show();
                                signup.setEnabled(true);
                                signup.setText("SIGNUP");
                                progressBar.setVisibility(View.INVISIBLE);
                                loginIntent.setVisibility(View.VISIBLE);
                                bottomNavigationView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(signupactivity.this, "Wrong Date of Birth", Toast.LENGTH_SHORT).show();
                            signup.setEnabled(true);
                            signup.setText("SIGNUP");
                            progressBar.setVisibility(View.INVISIBLE);
                            loginIntent.setVisibility(View.VISIBLE);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(signupactivity.this, "All Field Require", Toast.LENGTH_SHORT).show();
                        signup.setEnabled(true);
                        signup.setText("SIGNUP");
                        progressBar.setVisibility(View.INVISIBLE);
                        loginIntent.setVisibility(View.VISIBLE);
                        bottomNavigationView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(signupactivity.this, "Wrong Date of Birth", Toast.LENGTH_SHORT).show();
                    signup.setEnabled(true);
                    signup.setText("SIGNUP");
                    progressBar.setVisibility(View.INVISIBLE);
                    loginIntent.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

            }
        });
        //********************************************************************************


        //*******************************************************************************..
        //******************* Uploading profile *****************************************
        //*******************************************************************************
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

    }

    //************************** Get file Extension *********************************
    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }


    //************************* Activity Imge Choosing action ****************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImg);
        }
    }


    //************************* Open File Chooser **************************************
    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    // Uploading profile image....
    public void UploadFile() {

    }



    //********************** checking exsitence of username*******************************
    public boolean usernameExistace(String usernameText) {
        boolean isMatched = false;
        for (int i = 0; i < usernamelisttocheck.size(); i++) {
            if (usernameText.equals(usernamelisttocheck.get(i))) {
                isMatched = true;
            }
        }
        return isMatched;
    }

    //********************** checking username strong ************************************
    public boolean usernameStrong(String uName) {
        boolean isAlpha = false, isDigit = false, isCapable = false;
        for (int i = 0; i < uName.length(); i++) {
            if (Character.isDigit(uName.charAt(i))) {
                isDigit = true;
            }
            if (Character.isLetter(uName.charAt(i))) {
                isAlpha = true;
            }
        }
        return isAlpha && isDigit ? true : false;
    }


    // ******* Capatlize name *****************
    public static String capitalizeWord(String str){
        if (str.length()>0) {
            String words[]=str.split(" ");
            String capitalizeWord="";
            for(String w:words){
                String first=w.substring(0,1);
                String afterfirst=w.substring(1);
                capitalizeWord+=first.toUpperCase()+afterfirst+" ";
            }
            return capitalizeWord.trim();
        } else {
            return "Anonymous Name";
        }
    }

    //****************************************
    public boolean isDateFormetted(String d, String m, String y) {

        int dt = Integer.parseInt(d);
        int mt = Integer.parseInt(m);
        int yr = Integer.parseInt(y);

        if(dt>0 && dt<32 && mt>0 && mt<13 && yr<2020 && yr>1900) {
            return true;
        } else {
            return false;
        }
    }

}




























