package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class viewallpostactivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, postRefrence, likeReference, allpostRefrence, resetRefer;

    String friendUniqueKey = "", username = "", profile = "";

    ListView postview;

    ImageView freindfProfile, friendPostImage, likeButton, commentButton;
    TextView friendUsername, friendCaption, friendPostUploadDate, numberOfLikeTextview;

    int listviewscrollpostion = 0;

    String nameOfView = "";

    ArrayList<String> allUsernameList, allCaptionList, allPostDateList, allPostImageUrlList, allPostProfileImageUrl, allUniqueIdList, allPostLike, postUniqueId;

    ArrayList<String> useridList, postidList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewallpostactivity);

        Intent intent = getIntent();
        nameOfView = intent.getStringExtra("currentuserUsername");

        Log.i("username", nameOfView);
        //***********************************************************
        //*******  Firebase Database*******************************
        firebaseDatabase = FirebaseDatabase.getInstance();
        allpostRefrence = firebaseDatabase.getReference("All_Post");

        //*************** intializinf array list *******************
        allUsernameList = new ArrayList<String>();
        allCaptionList = new ArrayList<String>();
        allPostDateList = new ArrayList<String>();
        allPostImageUrlList = new ArrayList<String>();
        allUniqueIdList = new ArrayList<String>();
        allPostLike = new ArrayList<String>();
        postUniqueId = new ArrayList<String>();
        allPostProfileImageUrl = new ArrayList<String>();
        useridList = new ArrayList<String>();
        postidList = new ArrayList<String>();

        //*********************************************************

        postview = findViewById(R.id.allpostlistview);


        resetRefer = firebaseDatabase.getReference("Post");
        resetRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                allUsernameList.clear();
                allCaptionList.clear();
                allPostProfileImageUrl.clear();
                allPostDateList.clear();
                allPostLike.clear();
                allUniqueIdList.clear();
                allPostImageUrlList.clear();
                postUniqueId.clear();
                useridList.clear();
                postidList.clear();

                reset();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //*************************************************************************


    }


    public class CustomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allPostDateList.size();
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

            int len = allPostImageUrlList.size();

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


            //*****************
            friendUsername.setText(allUsernameList.get(pos));
            friendCaption.setText(allCaptionList.get(pos));
            numberOfLikeTextview.setText(allPostLike.get(pos));

            friendPostUploadDate.setText(allPostDateList.get(pos));

            Glide.with(viewallpostactivity.this)
                    .load(allPostProfileImageUrl.get(pos))
                    .into(freindfProfile);

            Glide.with(viewallpostactivity.this)
                    .load(allPostImageUrlList.get(pos))
                    .into(friendPostImage);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    likeButton.setImageResource(R.drawable.ic_likemain);

                    listviewscrollpostion = position;

                    likeReference = firebaseDatabase.getReference("Post").child(useridList.get(pos)).child(postidList.get(pos));

                    Post withPost = new Post(postidList.get(pos),
                            allCaptionList.get(pos),
                            allPostImageUrlList.get(pos),
                            allPostDateList.get(pos),
                            String.valueOf(Integer.parseInt(allPostLike.get(pos)) + 1));

                    likeReference.setValue(withPost);

                    Toast.makeText(viewallpostactivity.this, "Like", Toast.LENGTH_SHORT).show();

                    //***********************************************************************

                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View v) {
                    commentButton.setImageResource(R.drawable.ic_insert_comment_black_24dp);

                    Intent intent = new Intent(viewallpostactivity.this, commentactivity.class);
                    intent.putExtra("postuniqueid", postidList.get(pos));
                    intent.putExtra("username", allUsernameList.get(pos));
                    intent.putExtra("date", allPostDateList.get(pos));
                    intent.putExtra("caption", allCaptionList.get(pos));
                    intent.putExtra("posturl", allPostImageUrlList.get(pos));
                    intent.putExtra("profileurl", allPostProfileImageUrl.get(pos));
                    intent.putExtra("currentUser", nameOfView);
                    startActivity(intent);
//
//                    final DatabaseReference commentRefrence = FirebaseDatabase.getInstance().getReference("Comments").child(postidList.get(pos));
//
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(viewallpostactivity.this);
//                    alertDialog.setTitle("COMMENT BOX");
//                    alertDialog.setMessage("Enter your comment for this post.");
//
//                    final EditText input = new EditText(viewallpostactivity.this);
//                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT);
//                    input.setLayoutParams(lp);
//                    input.setHint("Comment");
//                    input.setLeftTopRightBottom(10,0, 0, 0);
//                    alertDialog.setView(input);
//                    alertDialog.setIcon(R.drawable.ic_insert_comment_black_24dp);
//
//                    alertDialog.setPositiveButton("Comment",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String commentText = input.getText().toString();
//                                    String date = new SimpleDateFormat("MMMM d, yyyy ", Locale.getDefault()).format(new Date());
//
//                                    String commentKey  = commentRefrence.push().getKey();
//                                    Comment comment = new Comment(commentKey, nameOfView,commentText, date);
//                                    commentRefrence.child(commentKey).setValue(comment);
//
//                                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    alertDialog.setNegativeButton("Cancel", null);
//                    alertDialog.show();


//                    Toast.makeText(viewallpostactivity.this, "Currently Not Avalible", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;

        }
    }


    public void reset() {


        allpostRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                allUsernameList.clear();
                allCaptionList.clear();
                allPostProfileImageUrl.clear();
                allPostDateList.clear();
                allPostLike.clear();
                allUniqueIdList.clear();
                allPostImageUrlList.clear();
                postUniqueId.clear();
                useridList.clear();
                postidList.clear();

                for (DataSnapshot allPostSnap : dataSnapshot.getChildren()) {
                    AllPost allPost = allPostSnap.getValue(AllPost.class);
                    final String userId = allPost.getIdOfUser();
                    final String postId = allPost.getIdOfPost();

                    //*****************************************************************************
                    //************* View User Table Database **************************************
                    //*****************************************************************************
                    databaseReference = firebaseDatabase.getReference("Users").child(userId);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            User user = dataSnapshot.getValue(User.class);

                            username = user.getUsername();
                            profile = user.getProfileImageURL();

                            allPostProfileImageUrl.add(profile);
                            allUsernameList.add(username);

                            Log.i("Users", username);
                            //*****************************************************************
                            //******************** Post Refernece *****************************
                            //*****************************************************************
                            postRefrence = firebaseDatabase.getReference("Post").child(userId).child(postId);
                            postRefrence.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    postview.smoothScrollToPosition(listviewscrollpostion);

                                    Post post = dataSnapshot.getValue(Post.class);

                                    if(post!=null) {
                                        useridList.add(userId);
                                        postidList.add(postId);
                                        allCaptionList.add(post.getPostCaption());
                                        allPostImageUrlList.add(post.getPostImgUrl());
                                        allPostDateList.add(post.getPostDate());
                                        allUniqueIdList.add(postId);
                                        allPostLike.add(post.getPostLike());
                                        postUniqueId.add(userId);
                                    }


                                    CustomeAdapter customeAdapter = new CustomeAdapter();
                                    postview.setAdapter(customeAdapter);

                                    if (customeAdapter.getCount() == 0) {
                                        Toast.makeText(viewallpostactivity.this, "No Post from " + username, Toast.LENGTH_SHORT).show();
                                    }

                                    postview.setSelection(listviewscrollpostion);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.i("Post error", databaseError.toString());
                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("User error", databaseError.toString());
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
