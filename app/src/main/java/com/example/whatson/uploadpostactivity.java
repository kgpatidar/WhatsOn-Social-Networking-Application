package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class uploadpostactivity extends AppCompatActivity {

    String captionText = "";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, databaseReference1, allPostRefrence;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ImageView postImage;
    EditText postCaption;
    Button uploadPostButton;
    TextView getBack;

    String nameData, usernameData, passwordData, imgUrlData, dobData, genderData, numberOfPostData;

    int numberofpostinteger = 0;

    Uri imageUri = null;
    String postImageUri;
    boolean isFileChoosed = false;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpostactivity);


        Intent intent = getIntent();
        final String currentUserId = intent.getStringExtra("useruniqueid");

        //********************* Accessing Firease *************************

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference1 = firebaseDatabase.getReference("Users").child(currentUserId);
        databaseReference = firebaseDatabase.getReference("Post").child(currentUserId);
        allPostRefrence = firebaseDatabase.getReference("All_Post");
        storageReference = firebaseStorage.getReference("Post_images").child(currentUserId);

        //************** Getting UI with ************************

        postImage = findViewById(R.id.newpostimageview);
        postCaption = findViewById(R.id.captionEditText);
        uploadPostButton = findViewById(R.id.uploadpostbutton1);
        getBack = findViewById(R.id.getbackprofileIntent);
        progressBar = findViewById(R.id.postuploadingprogress);
        progressBar.setVisibility(View.INVISIBLE);


        //******************************************************************************
        //********************** Database Referencing **********************************
        //******************************************************************************
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                // storring user data into variable
                nameData = user.getName();
                usernameData = user.getUsername();
                passwordData = user.getPassword();
                dobData = user.getDateofbirth();
                imgUrlData = user.getProfileImageURL();
                passwordData = user.getPassword();
                genderData = user.getGender();
                numberOfPostData = user.getNumberOfPost();

                numberofpostinteger = Integer.parseInt(numberOfPostData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //*******************************************************************
        //**************** On clicking Upload Post Button *******************
        //*******************************************************************
        uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getBack.setVisibility(View.INVISIBLE);
                uploadPostButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.bringToFront();
                progressBar.setProgress(100);

                captionText = postCaption.getText().toString().trim();

                //******************************************************************************
                //********************  Uploading Profile Picture ******************************
                //******************************************************************************

                final String postKey = databaseReference.push().getKey();

                if (isFileChoosed) {

                    final StorageReference fileRefrence = storageReference.child(postKey +
                            "." + getFileExtension(imageUri));

                    fileRefrence.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    numberofpostinteger++;

                                    numberOfPostData = String.valueOf(numberofpostinteger);

                                    postImageUri = taskSnapshot.getUploadSessionUri().toString();
                                    //***********************************************************************
                                    fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            postImageUri = uri.toString();
                                            String date = new SimpleDateFormat("MMMM d, yyyy ", Locale.getDefault()).format(new Date());

                                            Post post = new Post(postKey, captionText, postImageUri, date, "0");

                                            databaseReference.child(postKey).setValue(post);

                                            User newUser = new User(currentUserId, nameData, usernameData, passwordData, imgUrlData, dobData, genderData, numberOfPostData);

                                            AllPost allPost = new AllPost(currentUserId, postKey);

                                            allPostRefrence.child(postKey).setValue(allPost);

                                            databaseReference1.setValue(newUser);

                                            Toast.makeText(uploadpostactivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();


                                            //************** INcreasing Number of post ************************************


                                            Intent intent = new Intent(uploadpostactivity.this, userprofile.class);
                                            intent.putExtra("uniqueidofcurrentuser", currentUserId);
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
                                    uploadPostButton.setText("Uploading...");
//                                                                            Toast.makeText(signupactivity.this, "Almost Ready!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    Toast.makeText(uploadpostactivity.this, "Post can't  Uploaded!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    uploadPostButton.setEnabled(true);
                                    getBack.setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    Toast.makeText(uploadpostactivity.this, "No Selected File", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    uploadPostButton.setEnabled(true);
                    getBack.setVisibility(View.VISIBLE);
                }

            }
        });

        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(uploadpostactivity.this, userprofile.class);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                intent.putExtra("uniqueidofcurrentuser", currentUserId);
                startActivity(intent);
                finish();
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFileChoosed = true;
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
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(postImage);
        }
    }


    //************************* Open File Chooser **************************************
    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
}
