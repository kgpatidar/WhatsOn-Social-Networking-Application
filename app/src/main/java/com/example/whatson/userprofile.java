package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import is.arontibo.library.ElasticDownloadView;

public class userprofile extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, allUserReference;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    StorageReference storageReference;

    // UI intializing
    ImageView profileImage, whasOnlogo;
    ImageView editOption, viewAllUser;
    TextView nameOfCurrentUser, dobCurrentUser, usernameCurrentUser;
    Button viewMyPost, viewAllPost, updateMyPost;
    //    ProgressBar progressBar;
    ElasticDownloadView elasticDownloadView;
    RelativeLayout mainLayout;
    Uri imageUri = null;

    String usernameofcurrentuser = "";

    //  ****** database into varible****************
    String nameData, usernameData, passwordData, imgUrlData, dobData, genderData, numberOfPostData;
    String profileImageUri = null;
    String currentUserId;
    ArrayList<String> usernamelisttocheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        //********** intefacing UI and data***************************

        mainLayout = findViewById(R.id.mainuserlayout);
        mainLayout.setVisibility(View.INVISIBLE);

        elasticDownloadView = findViewById(R.id.progressonuser);
        elasticDownloadView.bringToFront();
        elasticDownloadView.startIntro();
        elasticDownloadView.success();

        profileImage = findViewById(R.id.profilepictureinprofileactivity);
        whasOnlogo = findViewById(R.id.whatsonlogoonprofile);
        editOption = findViewById(R.id.editlogoimageforprofile);
        nameOfCurrentUser = findViewById(R.id.nameofcurrentuserprofileactivity);
        dobCurrentUser = findViewById(R.id.dateofbirthprofileactivity);
        viewAllPost = findViewById(R.id.viewallpostbutton);
        viewMyPost = findViewById(R.id.viewmypostbutton);
        updateMyPost = findViewById(R.id.uploadpostbutton);
        usernameCurrentUser = findViewById(R.id.usernameuserprofiletextview);
        viewAllUser = findViewById(R.id.viewalluserinageview);

        //*********** catching intent *********************************

        final Intent intent = getIntent();
        currentUserId = intent.getStringExtra("uniqueidofcurrentuser");

        usernamelisttocheck = new ArrayList<String>();

        //*************************************************************

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(currentUserId);
        allUserReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("profilepicture");
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("loginaccount", 0);

        //************************************************************************************************
        //************************ refrenceing Current User Database database ***************************
//************************************************************************************************
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameOfCurrentUser.setText(user.getName());
                dobCurrentUser.setText(user.getDateofbirth());
                usernameCurrentUser.setText(user.getUsername());
                // storring user data into variable

                nameData = user.getName();
                usernameData = user.getUsername();
                passwordData = user.getPassword();
                dobData = user.getDateofbirth();
                imgUrlData = user.getProfileImageURL();
                passwordData = user.getPassword();
                genderData = user.getGender();
                numberOfPostData = user.getNumberOfPost();


                profileImageUri = imgUrlData;
                //************************************************************************************************
                //************************ Shwoing Profile Picture in Imageview ***************************
                //************************************************************************************************
                Glide.with(getApplicationContext())
                        .load(imgUrlData)
                        .into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //****************************************************************************
        //***************  View All User Intent **************************************
        //****************************************************************************
        viewAllUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userprofile.this, allusersactivity.class);
                intent.putExtra("useruniqueid", currentUserId);
                intent.putExtra("currentuserfullname", usernameData);
                startActivity(intent);
            }
        });

        viewMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(userprofile.this, viewmypostactivity.class);
                intent1.putExtra("useruniqueid", currentUserId);
                startActivity(intent1);
            }
        });


        viewAllPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(userprofile.this, viewallpostactivity.class);
                intent1.putExtra("currentuserUsername", usernameData);
                startActivity(intent1);
            }
        });
        //****************************************************************************
        //***************  Upload  New Post User Intent **************************************
        //****************************************************************************
        updateMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userprofile.this, uploadpostactivity.class);
                intent.putExtra("useruniqueid", currentUserId);
                startActivity(intent);
                finish();
            }
        });

        //************************************************************************************************
        //************************ refrenceing Current User Database database ***************************
        //************************************************************************************************
        allUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String allUser = dataSnapshot.getValue(User.class).getUsername();

                    usernamelisttocheck.add(allUser);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//************************************************************************************************
        //**********  Edit Option ************************************************
        //************************************************************************************************

        editOption.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {

                editOption.animate().rotationBy(90).setDuration(1000);

                PopupMenu popup = new PopupMenu(userprofile.this, v);
                final MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_main, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            //************************************************************************************************
                            //**************************************** Updateing New Username  ********************************************************
                            //************************************************************************************************
                            case R.id.editusername:
                                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(userprofile.this);
                                alertDialog1.setTitle("   CHANGE USERNAME");
                                alertDialog1.setMessage("Enter your New Username and Click Change.");

                                final EditText inputTxt = new EditText(userprofile.this);
                                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                inputTxt.setLayoutParams(lp1);
                                inputTxt.setTextAlignment(LinearLayout.TEXT_ALIGNMENT_CENTER);
                                alertDialog1.setView(inputTxt);
                                alertDialog1.setIcon(R.drawable.ic_email_black_24dp);

                                alertDialog1.setPositiveButton("CHANGE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                //***************************************************************
                                                //***********  Updating Username ********************************
                                                //***************************************************************

                                                if (usernameStrong(inputTxt.getText().toString()) || !TextUtils.isEmpty(inputTxt.getText())) {
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    // Get auth credentials from the user for re-authentication
                                                    AuthCredential credential = EmailAuthProvider
                                                            .getCredential(usernameData, passwordData); // Current Login Credentials \\
                                                    // Prompt the user to re-provide their sign-in credentials
                                                    user.reauthenticate(credential)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Log.d("Authentication", "User re-authenticated.");
                                                                    //Now change your email address \\
                                                                    //----------------Code for Changing Email Address----------\\
                                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                                    final String perfextUsername = inputTxt.getText().toString() + "@gmail.com";

                                                                    user.updateEmail(perfextUsername)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {

                                                                                        User newUser = new User(currentUserId, nameData, perfextUsername, passwordData, imgUrlData, dobData, genderData, numberOfPostData);
                                                                                        databaseReference.setValue(newUser);

                                                                                        new AlertDialog.Builder(userprofile.this)
                                                                                                .setTitle("YOUR USERNAME CHANGE SUCCESSFULLY")
                                                                                                .setIcon(R.drawable.ic_verified_user_black_24dp)
                                                                                                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        // Continue with delete operation

                                                                                                    }
                                                                                                })
                                                                                                .show();

                                                                                    } else {


                                                                                        new AlertDialog.Builder(userprofile.this)
                                                                                                .setTitle("USERNAME IS NOT CHANGED")
                                                                                                .setIcon(android.R.drawable.ic_menu_edit)
                                                                                                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        // Continue with delete operation

                                                                                                    }
                                                                                                })
                                                                                                .show();


                                                                                    }
                                                                                }
                                                                            });
                                                                    //----------------------------------------------------------\\
                                                                }
                                                            });
                                                } else {

                                                    new AlertDialog.Builder(userprofile.this)
                                                            .setTitle("USERNAME MUST CONTAIN NUMBER AND ALPHABATE")
                                                            .setIcon(android.R.drawable.stat_notify_error)
                                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Continue with delete operation
                                                                }
                                                            })
                                                            .show();

                                                }


                                            }
                                        });
                                alertDialog1.setNegativeButton("Cancel", null);
                                alertDialog1.show();
                                break;


                            //************************************************************************************************
                            //**************************************** Updateing New Password  ********************************************************
                            //************************************************************************************************
                            case R.id.editpassword:

                                // editing detail intent
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(userprofile.this);
                                alertDialog.setTitle("   CHANGE PASSWORD");
                                alertDialog.setMessage("Enter your current Passwrod and Click CHECK");

                                final EditText input = new EditText(userprofile.this);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                input.setTextAlignment(LinearLayout.TEXT_ALIGNMENT_CENTER);
                                alertDialog.setView(input);
                                alertDialog.setIcon(R.drawable.ic_vpn_key_black_24dp);


                                alertDialog.setPositiveButton("CHECK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                // ***************changing password********************
                                                //************************************************************************************************
                                                final String currentEnterepass = input.getText().toString();
                                                if (currentEnterepass.equals(passwordData)) {

                                                    //************************************************************************************************
                                                    // ************** on mathing Current password ****************
                                                    //************************************************************************************************

                                                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(userprofile.this);
                                                    alertDialog.setTitle("CHANGE PASSWORD");
                                                    alertDialog.setMessage("Enter NEW Passwrod and click CHANGE");

                                                    final EditText currentPassText = new EditText(userprofile.this);
                                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                                    currentPassText.setLayoutParams(lp);
                                                    currentPassText.setTextAlignment(LinearLayout.TEXT_ALIGNMENT_CENTER);
                                                    alertDialog.setView(currentPassText);
                                                    alertDialog.setIcon(R.drawable.ic_vpn_key_black_24dp);
                                                    final String[] password = new String[1];
                                                    alertDialog.setPositiveButton("CHANGE",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    //*****************************
                                                                    //************************************************************************************************
                                                                    //************************************************************************************************
                                                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                                    AuthCredential credential = EmailAuthProvider
                                                                            .getCredential(firebaseAuth.getCurrentUser().getEmail(), currentEnterepass);

                                                                    user.reauthenticate(credential)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        user.updatePassword(currentPassText.getText().toString())
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {

                                                                                                            //************************************************************************************************
                                                                                                            //***************** on success ****************
                                                                                                            //************************************************************************************************


                                                                                                            User updateUser = new User(currentUserId, nameData, usernameData, currentPassText.getText().toString(), imgUrlData, dobData, genderData, numberOfPostData);
                                                                                                            databaseReference.setValue(updateUser);

                                                                                                            new AlertDialog.Builder(userprofile.this)
                                                                                                                    .setTitle("YOUR PASSWORD CHANGE SUCCESSFULLY")
                                                                                                                    .setIcon(R.drawable.ic_verified_user_black_24dp)
                                                                                                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                            // Continue with delete operation

                                                                                                                        }
                                                                                                                    })
                                                                                                                    .show();


                                                                                                        } else {

                                                                                                            //************************************************************************************************
                                                                                                            //*********** On fail ***************
                                                                                                            //************************************************************************************************
                                                                                                            new AlertDialog.Builder(userprofile.this)
                                                                                                                    .setTitle("PASSWORD NOT CHANGE!")
                                                                                                                    .setIcon(android.R.drawable.stat_notify_error)
                                                                                                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                                                            // Continue with delete operation

                                                                                                                        }
                                                                                                                    })
                                                                                                                    .show();

                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    } else {

                                                                                        //************************************************************************************************
                                                                                        //*********** on fail of authentication
                                                                                        //************************************************************************************************

                                                                                        new AlertDialog.Builder(userprofile.this)
                                                                                                .setTitle("Authentication Failed!")
                                                                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                                                                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                                        // Continue with delete operation

                                                                                                    }
                                                                                                })
                                                                                                .show();

                                                                                    }
                                                                                }
                                                                            });


                                                                }
                                                            });


                                                    alertDialog.setNegativeButton("Cancel", null);
                                                    alertDialog.show();


                                                } else {
                                                    Toast.makeText(userprofile.this, "Password Not Matched", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                alertDialog.setNegativeButton("Cancel", null);
                                alertDialog.show();
                                break;


                            //************************************************************************************************
                            //**********************************  On clicking SIGNOUT button  **************************************************************
                            //************************************************************************************************
                            case R.id.signoutmenu:
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("currentuserusername", "");
                                editor.putString("currentuserpassword", "");
                                editor.commit();
                                firebaseAuth.getInstance().signOut();
                                Intent intent1 = new Intent(userprofile.this, loginactivity.class);
                                startActivity(intent1);
                                finish();
                                break;


                            //************************************************************************************************
                            //**************************************  Closing Application **********************************************************
                            //************************************************************************************************
                            case R.id.closemenu:
                                finish();
                                break;


                            //************************************************************************************************
                            //***************************************** On clicking contact *******************************************************
                            //************************************************************************************************
                            case R.id.contactusmenu:
                                Toast.makeText(userprofile.this, "Contact", Toast.LENGTH_SHORT).show();
                                break;
                        }

                        return true;
                    }
                });

            }
        });


        //************************************************************************************************
        //********************************************* On changing Name of Current User **********************************************
        //************************************************************************************************
        nameOfCurrentUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(userprofile.this);
                alertDialog.setTitle("   CHANGE NAME OF USER");
                alertDialog.setMessage("  Enter Your New Name.");
                alertDialog.setIcon(android.R.drawable.ic_menu_edit);
                final EditText input1 = new EditText(userprofile.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input1.setLayoutParams(lp);
                input1.setTextAlignment(LinearLayout.TEXT_ALIGNMENT_CENTER);
                alertDialog.setView(input1);
                alertDialog.setPositiveButton("CHANGE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (!TextUtils.isEmpty(input1.getText())) {


                                    // ********************* changing user full name **********************
                                    User updateUser = new User(currentUserId, capitalizeWord(input1.getText().toString()), usernameData, passwordData, imgUrlData, dobData, genderData, numberOfPostData);
                                    databaseReference.setValue(updateUser);

                                    new AlertDialog.Builder(userprofile.this)
                                            .setTitle("YOUR NAME CHANGE SUCCESSFULLY!")
                                            .setIcon(R.drawable.ic_verified_user_black_24dp)
                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Continue with delete operation
                                                }
                                            })
                                            .show();
                                } else {
                                    new AlertDialog.Builder(userprofile.this)
                                            .setTitle("Empty Field!")
                                            .setIcon(android.R.drawable.stat_notify_error)
                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Continue with delete operation
                                                }
                                            })
                                            .show();
                                }
                            }

                        });
                alertDialog.setNegativeButton("Cancel", null);
                alertDialog.show();
            }
        });


        //*****************************************************************************
        //************************** Chnaging Profile Picture *************************
        //*****************************************************************************

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //***************** Asking for judgement ========================

                new AlertDialog.Builder(userprofile.this)
                        .setTitle("CHANGING PROFILE")
                        .setMessage("Do you want to change your Profile Picture?")
                        .setIcon(R.drawable.ic_perm_identity_black_24dp)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                openFileChooser();


                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });


        //*************************************************************************************
        // ***************** whats on logo **************************************

        whasOnlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                whasOnlogo.animate().rotationBy(360).setDuration(1000);

            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                elasticDownloadView.setVisibility(View.INVISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
            }
        }, 3000);


    }

    //*************************************************************
    //****************** all about chnaging profile **********
    //*************************************************************

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    //************************* Activity Imge Choosing action ****************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImage);

            Log.i("profile message", "1");

            String storageUrl = imgUrlData;
            final StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl);
            Log.i("profile message", "2");
            final StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("profilepicture");
            Log.i("profile message", "3");
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                    //***************************************************************
                    if (profileImage != null) {

                        Log.i("profile message", "4");

                        final StorageReference fileRefrence = storageReference1.child(currentUserId +
                                "." + getFileExtension(imageUri));

                        Log.i("profile message", "5");

                        fileRefrence.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        Log.i("profile message", "6");

                                        profileImageUri = taskSnapshot.getUploadSessionUri().toString();
                                        //***********************************************************************
                                        fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                Log.i("profile message", "7");

                                                profileImageUri = uri.toString();
                                                User user = new User(currentUserId, nameData, usernameData, passwordData, profileImageUri, dobData, genderData, numberOfPostData);
                                                databaseReference.setValue(user);

                                                Log.i("profile message", "8");

                                                new AlertDialog.Builder(userprofile.this)
                                                        .setIcon(R.drawable.ic_verified_user_black_24dp)
                                                        .setTitle("Your Profile Changed Successfully!")
                                                        .setPositiveButton("Done", null);

                                                Log.i("profile message", "9");

                                            }
                                        });
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        elasticDownloadView.startIntro();
                                        Log.i("profile message", "10");
                                    }
                                })
                                .addOnCanceledListener(new OnCanceledListener() {
                                    @Override
                                    public void onCanceled() {
                                        Log.i("profile message", "11");
                                        Toast.makeText(userprofile.this, "Image Fail!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Log.i("profile message", "11");
                        Toast.makeText(userprofile.this, "No Selected File", Toast.LENGTH_SHORT).show();
                    }

                    //******************************************************************
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("profile message", "11");
                    Toast.makeText(userprofile.this, "Failed" + e.toString(), Toast.LENGTH_SHORT).show();

                }
            });


        }












    }


    //************************* Open File Chooser **************************************
    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    // ******* Capatlize name *****************
    public static String capitalizeWord(String str) {
        if (str.length() > 0) {
            String words[] = str.split(" ");
            String capitalizeWord = "";
            for (String w : words) {
                String first = w.substring(0, 1);
                String afterfirst = w.substring(1);
                capitalizeWord += first.toUpperCase() + afterfirst + " ";
            }
            return capitalizeWord.trim();
        } else {
            return null;
        }
    }

    //***************  Strong Username *********************
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

    //********** username existance ***********************
    public boolean usernameExistace(String usernameText) {
        boolean isMatched = false;
        for (int i = 0; i < usernamelisttocheck.size(); i++) {
            if (usernameText.equals(usernamelisttocheck.get(i))) {
                isMatched = true;
            }
        }
        return isMatched;
    }


}
