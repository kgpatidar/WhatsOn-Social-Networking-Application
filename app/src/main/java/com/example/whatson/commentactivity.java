package com.example.whatson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

public class commentactivity extends AppCompatActivity {

    String postId = "", currentUser = "";
    DatabaseReference commentReference, postRefrence;

    ListView commentlist;

    ImageView profilepic, postpic;
    TextView caption1, date1, username1;

    ImageView commentButton;
    EditText commentEditText;

    TextView txtDate, txtComment, txtCommenter;

    ArrayList<String> commentText, commentDate, commentUser, commentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentactivity);

        profilepic = findViewById(R.id.profileofcommentload);
        postpic = findViewById(R.id.postimageofcommentload);
        caption1 = findViewById(R.id.captionofcommentload);
        date1 = findViewById(R.id.dateofuploadofcommentload);
        username1 = findViewById(R.id.usernameofcommentload);
        commentEditText = findViewById(R.id.commentedittextview);
        commentButton = findViewById(R.id.commentbutton);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postuniqueid");
        currentUser = intent.getStringExtra("currentUser");

        Glide.with(commentactivity.this)
                .load(intent.getStringExtra("profileurl"))
                .into(profilepic);

        Glide.with(commentactivity.this)
                .load(intent.getStringExtra("posturl"))
                .into(postpic);

        final String userName = intent.getStringExtra("username");

        caption1.setText(intent.getStringExtra("caption"));
        date1.setText(intent.getStringExtra("date"));
        username1.setText(intent.getStringExtra("username"));

        //****************************************************************************
        //************  Comment array list *******************************************
        commentText = new ArrayList<String>();
        commentDate = new ArrayList<String>();
        commentId = new ArrayList<String>();
        commentUser = new ArrayList<String>();

        //***************************************************************************
        commentlist = findViewById(R.id.commentlistview);

        commentReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentUser.clear();
                commentId.clear();
                commentDate.clear();
                commentText.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);

                    commentId.add(comment.getCommentUniqueID());
                    commentDate.add(comment.getCommentDate());
                    commentText.add(comment.getCommentText());
                    commentUser.add(comment.getCommentUser());

                    CustomeAdapter customeAdapter = new CustomeAdapter();
                    commentlist.setAdapter(customeAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getComment = commentEditText.getText().toString();

                if (!TextUtils.isEmpty(getComment)) {
                    String date = new SimpleDateFormat("MMMM d, yyyy ", Locale.getDefault()).format(new Date());
                    final DatabaseReference commentRefrence = FirebaseDatabase.getInstance().getReference("Comments").child(postId);
                    String commentKey = commentRefrence.push().getKey();
                    Comment comment = new Comment(commentKey, currentUser, getComment, date);
                    commentRefrence.child(commentKey).setValue(comment);
                    commentEditText.setText("");
                }
            }
        });

    }


    public class CustomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commentId.size();
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

            int pos = commentDate.size() - position - 1;

            convertView = getLayoutInflater().inflate(R.layout.customrlayoutcomment, null);

            txtComment = convertView.findViewById(R.id.commenttextview);
            txtCommenter = convertView.findViewById(R.id.commentpostertextview);
            txtDate = convertView.findViewById(R.id.commentdateviewr);

            txtCommenter.setText(commentUser.get(pos));
            txtComment.setText(commentText.get(pos));
            txtDate.setText(commentDate.get(pos));

            return convertView;
        }
    }
}
