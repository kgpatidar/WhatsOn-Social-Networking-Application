package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class viewmypostactivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, postRefrence, likeReference;

    String friendUniqueKey = "", username = "", profile = "";

    ListView postview;

    ImageView freindfProfile, friendPostImage, likeButton, commentButton;
    TextView friendUsername, friendCaption, friendPostUploadDate, numberOfLikeTextview;

    int listviewscrollpostion = 0;

    ArrayList<String> friendUsernameList, friendCaptionList, friendPostDateList, friendPostImageUrlList, friendUniqueIdList, friendPostLike, postUniqueId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmypostactivity);

        Intent intent = getIntent();
        friendUniqueKey = intent.getStringExtra("useruniqueid");

        //***********************************************************
        //*******  Firebase Database ********************************
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users").child(friendUniqueKey);
        postRefrence = firebaseDatabase.getReference("Post").child(friendUniqueKey);

        //*************** intializinf array list *******************
        friendUsernameList = new ArrayList<String>();
        friendCaptionList = new ArrayList<String>();
        friendPostDateList = new ArrayList<String>();
        friendPostImageUrlList = new ArrayList<String>();
        friendUniqueIdList = new ArrayList<String>();
        friendPostLike = new ArrayList<String>();
        postUniqueId = new ArrayList<String>();

        //*********************************************************

        postview = findViewById(R.id.allmypostlistview);

        ///********************* Getting data ************************

        reset();

        ///////////////////////////////////////////////////////////
        //******************* Deleting Post ***********************
        // you want ot delete the psot

     postview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
         @Override
         public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

             new AlertDialog.Builder(viewmypostactivity.this)
                     .setTitle("Post Delete")
                     .setMessage("Do you want to delete this post?")
                     .setIcon(android.R.drawable.ic_delete)
                     .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             //***********************************************************

                             StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                                     friendPostImageUrlList.get(friendPostImageUrlList.size()- position - 1));

                             final String postUnique = postUniqueId.get(friendPostImageUrlList.size()- position - 1);

                             photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {

                                     DatabaseReference feeddeleterreference = FirebaseDatabase.getInstance().getReference("Post").child(friendUniqueKey);
                                     feeddeleterreference.child(postUnique).removeValue();
                                     DatabaseReference allfeeddeleterreference = FirebaseDatabase.getInstance().getReference("All_Post");
                                     allfeeddeleterreference.child(postUnique).removeValue();

                                     reset();

                                     Toast.makeText(viewmypostactivity.this, "Post Deleted", Toast.LENGTH_SHORT).show();

                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {

                                 }
                             });


                             DatabaseReference feeddeleterreference = FirebaseDatabase.getInstance().getReference("Post").child(friendUniqueKey);
                             feeddeleterreference.child(postUniqueId.get(friendPostImageUrlList.size()- position - 1)).removeValue();
                             DatabaseReference allfeeddeleterreference = FirebaseDatabase.getInstance().getReference("All_Post");
                             allfeeddeleterreference.child(postUniqueId.get(friendPostImageUrlList.size()- position - 1)).removeValue();

                             reset();

                             Toast.makeText(viewmypostactivity.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                         }
                     })
                     .setNegativeButton("Cancel", null)
                     .show();


             return true;
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

            Glide.with(viewmypostactivity.this)
                    .load(profile)
                    .into(freindfProfile);

            Glide.with(viewmypostactivity.this)
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
                            String.valueOf(Integer.parseInt(friendPostLike.get(pos))+1));

                    postRefrence.child(postUniqueId.get(pos)).setValue(post);

                    //***********************************************************************
                    Toast.makeText(viewmypostactivity.this, "Liked", Toast.LENGTH_SHORT).show();
                }
            });

            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(viewmypostactivity.this, commentactivity.class);
                    intent.putExtra("postuniqueid", postUniqueId.get(pos));
                    intent.putExtra("username", username);
                    intent.putExtra("date", friendPostDateList.get(pos));
                    intent.putExtra("caption", friendCaptionList.get(pos));
                    intent.putExtra("posturl", friendPostImageUrlList.get(pos));
                    intent.putExtra("profileurl", profile);
                    intent.putExtra("currentUser", username);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }


    public void reset() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                username = user.getUsername();

                Log.i("username is ", username);

                profile = user.getProfileImageURL();

                if(postRefrence.getRoot()!=null) {
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

                                CustomeAdapter customeAdapter = new CustomeAdapter();
                                postview.setAdapter(customeAdapter);

                                if (friendUniqueIdList.size() == 0) {
                                    Toast.makeText(viewmypostactivity.this, "No Post from " + username, Toast.LENGTH_SHORT).show();
                                }

                                postview.setSelection(listviewscrollpostion);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //***************************************************************************

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    
}


// comment
    //- post id
       //- comment id
           //- comment post name
           //- comment text