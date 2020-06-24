package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class allusersactivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, allUserReference;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    StorageReference storageReference;

    //********************************
    ImageView allUserProfileImage;
    ListView allUserListview;
    TextView allUserUsername, allUserFullname, allUserNumberofpost;

    String currentUserId = "", currentUserUsernamne = "";
    Uri imageUri = null;


    ArrayList<String> allUserNameList, allUserUniqeIDList, allUserFullnameList, allUserNopList, allUserProfileImgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusersactivity);

        //************************* Getting intent and Userid ***************************
        final Intent intent = getIntent();
        currentUserId = intent.getStringExtra("useruniqueid");
        currentUserUsernamne = intent.getStringExtra("currentuserfullname");

        //*******************************************************************************

        //************************* UI with Data **********************************************

        allUserListview = findViewById(R.id.alluserlistview);

        //*****************************************************************************
        allUserNameList = new ArrayList<String>();
        allUserUniqeIDList = new ArrayList<String>();
        allUserFullnameList = new ArrayList<String>();
        allUserNopList = new ArrayList<String>();
        allUserProfileImgUrl = new ArrayList<String>();

        //******************  Firebase Connection all **************************************
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(currentUserId);
        allUserReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("profilepicture");
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getApplicationContext().getSharedPreferences("loginaccount", 0);

        //**************************************************************************************

        allUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUserNopList.clear();
                allUserUniqeIDList.clear();
                allUserNopList.clear();
                allUserFullnameList.clear();
                allUserProfileImgUrl.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if(!currentUserId.equals(user.getUniqueID())) {
                        allUserNameList.add(user.getUsername());
                        allUserUniqeIDList.add(user.getUniqueID());
                        allUserNopList.add(user.getNumberOfPost());
                        allUserFullnameList.add(user.getName());
                        allUserProfileImgUrl.add(user.getProfileImageURL());
                    }

                    CustomeAdapter customeAdapter = new CustomeAdapter();
                    allUserListview.setAdapter(customeAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //**************************************************************************************
        allUserListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //*************** Get new Intent to show profile
                Intent intent1 = new Intent(allusersactivity.this, particularuserprofile.class);
                intent1.putExtra("frienduniqueid", allUserUniqeIDList.get(position));
                intent1.putExtra("currentuserid", currentUserId);
                intent1.putExtra("currentuserfullname", currentUserUsernamne);
                startActivity(intent1);

            }
        });



    }

    public class CustomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allUserNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customelayoutusers, null);

            allUserFullname = convertView.findViewById(R.id.alluserfullname);
            allUserNumberofpost = convertView.findViewById(R.id.allusernumberofpost);
            allUserProfileImage = convertView.findViewById(R.id.alluserprofileimage);
            allUserUsername = convertView.findViewById(R.id.alluserUsername);

            allUserUsername.setText(allUserNameList.get(position));
            allUserFullname.setText(allUserFullnameList.get(position));
            allUserNumberofpost.setText(allUserNopList.get(position));

            //************* Loading Profile Images *************************************
            // From One Way
//            new userprofile.DownloadImageTask(allUserProfileImage).execute(allUserProfileImgUrl.get(position));

            // From Another Way
            Glide.with(allusersactivity.this)
                    .load(allUserProfileImgUrl.get(position))
                    .into(allUserProfileImage);


            return convertView;
        }
    }

}
