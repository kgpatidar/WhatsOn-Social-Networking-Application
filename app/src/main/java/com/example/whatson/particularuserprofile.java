package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class particularuserprofile extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, postRefrence, likeReference;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;

    String username = null, profile = "";

    ImageView profileImage;
    TextView friendName, friendDob;
    ListView postview;

    ImageView freindfProfile, friendPostImage, likeButton, commentButton;
    TextView friendUsername, friendCaption, friendPostUploadDate, numberOfLikeTextview;

    int listviewscrollpostion = 0;
    String friendUniqueKey, nameOfView = "", currentUserid = "";

    ImageView chat;

    ArrayList<String> friendUsernameList, friendCaptionList, friendPostDateList, friendPostImageUrlList, friendUniqueIdList, friendPostLike, postUniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particularuserprofile);

        final Intent intent = getIntent();
        friendUniqueKey = intent.getStringExtra("frienduniqueid");
        nameOfView = intent.getStringExtra("currentuserfullname");
        currentUserid = intent.getStringExtra("currentuserid");

        //*************  Getting UI *********************************
        profileImage = findViewById(R.id.profilepictureinparticular);
        friendName = findViewById(R.id.nameofcurrentuserparticular);
        friendDob = findViewById(R.id.dateofbirthparticular);
        postview = findViewById(R.id.postlistparticular);
        chat = findViewById(R.id.chatbutton);

        //*************** intializinf array list *******************
        friendUsernameList = new ArrayList<String>();
        friendCaptionList = new ArrayList<String>();
        friendPostDateList = new ArrayList<String>();
        friendPostImageUrlList = new ArrayList<String>();
        friendUniqueIdList = new ArrayList<String>();
        friendPostLike = new ArrayList<String>();
        postUniqueId = new ArrayList<String>();

        //********************************************************
        //***************  Database ********************************
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(friendUniqueKey);
        postRefrence = firebaseDatabase.getReference("Post").child(friendUniqueKey);
        ///********************* Getting data ************************
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                friendName.setText(user.getName());
                friendDob.setText(user.getDateofbirth());
                username = user.getUsername();

                profile = user.getProfileImageURL();

                Glide.with(getApplicationContext())
                        .load(profile)
                        .into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(particularuserprofile.this, chatactivity.class);
                intent1.putExtra("chatfriendusername", username);
                intent1.putExtra("chatfriendprofile", profile);
                intent1.putExtra("frienduniqueid", friendUniqueKey);
                intent1.putExtra("currentuserid", currentUserid);
                startActivity(intent1);
            }
        });
        //*****************************************************************
        //******************** Post Refernece *****************************
        //*****************************************************************
        postRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postview.scrollListBy(listviewscrollpostion);

                friendPostDateList.clear();
                friendCaptionList.clear();
                friendPostImageUrlList.clear();
                friendUniqueIdList.clear();
                friendPostLike.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Post post = snap.getValue(Post.class);

                    friendCaptionList.add(post.getPostCaption());
                    friendPostImageUrlList.add(post.getPostImgUrl());
                    friendPostDateList.add(post.getPostDate());
                    friendUniqueIdList.add(postRefrence.getKey());
                    friendPostLike.add(post.getPostLike());
                    postUniqueId.add(post.getPostUniqueId());
                }

                CustomeAdapter customeAdapter = new CustomeAdapter();
                postview.setAdapter(customeAdapter);

                if (customeAdapter.getCount() == 0) {
                    Toast.makeText(particularuserprofile.this, "No Post from " + username, Toast.LENGTH_SHORT).show();
                }

                postview.setSelection(listviewscrollpostion);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class CustomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return friendPostDateList.size();
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
        public View getView(final int position, View convertView, final ViewGroup parent) {

            int len = friendPostImageUrlList.size();

            final int pos = len - position - 1;

            convertView = getLayoutInflater().inflate(R.layout.customelayoutpost, null);

            friendUsername = convertView.findViewById(R.id.usernameofpostuploader);
            friendCaption = convertView.findViewById(R.id.captionofpostuploader);
            friendPostUploadDate = convertView.findViewById(R.id.dateofuploadofpostuploader);
            freindfProfile = convertView.findViewById(R.id.profileofpostuploader);
            friendPostImage = convertView.findViewById(R.id.postimageofpostuploader);
            likeButton = convertView.findViewById(R.id.likeofpostuploader);
            commentButton = convertView.findViewById(R.id.commentofpostuploader);
            numberOfLikeTextview = convertView.findViewById(R.id.numberoflikeofpostuploader);

            friendUsername.setText(username);  //*****************
            friendCaption.setText(friendCaptionList.get(pos));
            numberOfLikeTextview.setText(friendPostLike.get(pos));

            friendPostUploadDate.setText(friendPostDateList.get(pos));

            Glide.with(particularuserprofile.this)
                    .load(profile)
                    .into(freindfProfile);

            Glide.with(particularuserprofile.this)
                    .load(friendPostImageUrlList.get(pos))
                    .into(friendPostImage);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeButton.setImageResource(R.drawable.ic_likemain);

                    listviewscrollpostion = position;
                    //****** Increase number of likes*****************************************
                    likeReference = firebaseDatabase.getReference("Users").child(friendUniqueIdList.get(pos));

                    Post post = new Post(postUniqueId.get(pos),
                            friendCaptionList.get(pos),
                            friendPostImageUrlList.get(pos),
                            friendPostDateList.get(pos),
                            String.valueOf(Integer.parseInt(friendPostLike.get(pos)) + 1));

                    postRefrence.child(postUniqueId.get(pos)).setValue(post);

                    //***********************************************************************
                    Toast.makeText(particularuserprofile.this, "Liked", Toast.LENGTH_SHORT).show();
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View v) {
                    commentButton.setImageResource(R.drawable.ic_insert_comment_black_24dp);

                    Intent intent = new Intent(particularuserprofile.this, commentactivity.class);

                    intent.putExtra("postuniqueid", postUniqueId.get(pos));
                    intent.putExtra("username", username);
                    intent.putExtra("date", friendPostDateList.get(pos));
                    intent.putExtra("caption", friendCaptionList.get(pos));
                    intent.putExtra("posturl", friendPostImageUrlList.get(pos));
                    intent.putExtra("profileurl", profile);
                    intent.putExtra("currentUser", nameOfView);
                    startActivity(intent);

                }
            });

            return convertView;
        }
    }
}
