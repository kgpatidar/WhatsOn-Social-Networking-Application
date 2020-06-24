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
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class chatactivity extends AppCompatActivity {

    String currentUserId = "", friendId = "", finalChatid = "";

    ImageView profileImg, send;
    TextView friendUsername;
    EditText messageInput;

    TextView message, time;

    ListView chatlistview;

    DatabaseReference chatReference;

    ArrayList<String> messageList, senderList, recieverList, timeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactivity);

        //******************************************************
        profileImg = findViewById(R.id.chatprofileimage);
        friendUsername = findViewById(R.id.chatusername);
        send = findViewById(R.id.sendmessagebutton);
        messageInput = findViewById(R.id.messageEdittext);
        chatlistview = findViewById(R.id.chatlistview);
        chatlistview.setDivider(null);
        chatlistview.setDividerHeight(10);
        //******************************************************

        messageList = new ArrayList<String>();
        senderList = new ArrayList<String>();
        recieverList = new ArrayList<String>();
        timeList = new ArrayList<String>();

        //****************************************************

        Intent intent = getIntent();

        currentUserId = intent.getStringExtra("currentuserid");
        friendId = intent.getStringExtra("frienduniqueid");

        String[] uniqueIDs = new String[2];
        uniqueIDs[0] = currentUserId;
        uniqueIDs[1] = friendId;

        Arrays.sort(uniqueIDs);

        finalChatid = uniqueIDs[0] + "_" + uniqueIDs[1];

        //********************************** Database ***********************************
        chatReference = FirebaseDatabase.getInstance().getReference("Chats").child(finalChatid);

        Glide.with(getApplicationContext())
                .load(intent.getStringExtra("chatfriendprofile"))
                .into(profileImg);

        friendUsername.setText(intent.getStringExtra("chatfriendusername"));

        //*********************************************************************************


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = messageInput.getText().toString();

                String messageKey = chatReference.push().getKey();

                if(!TextUtils.isEmpty(messageTxt)) {

                    String currentTime = new SimpleDateFormat("d/MMM/yyyy, h:mm a", Locale.getDefault()).format(new Date());

                    Chat chat = new Chat(messageKey, messageTxt, currentUserId, friendId, currentTime);

                    chatReference.child(messageKey).setValue(chat);


                } else {

                }


                messageInput.setText("");
            }
        });

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                senderList.clear();
                recieverList.clear();
                timeList.clear();
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    Chat chat = snap.getValue(Chat.class);

                    messageList.add(chat.getMessage());
                    senderList.add(chat.getSenderId());
                    recieverList.add(chat.getRecieverId());
                    timeList.add(chat.getTimeSend());

                    CustomeAdapter customeAdapter = new CustomeAdapter();
                    chatlistview.setAdapter(customeAdapter);

                    chatlistview.setStackFromBottom(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public class CustomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return timeList.size();
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

            String recieveID = recieverList.get(position);
            String senderID = senderList.get(position);

            if(senderID.equals(currentUserId)) {
                convertView = getLayoutInflater().inflate(R.layout.messagesender, null);
            } else {
                convertView = getLayoutInflater().inflate(R.layout.messagereciever, null);
            }

            message = convertView.findViewById(R.id.messageTextview);
            time = convertView.findViewById(R.id.datetextview);

            message.setText(messageList.get(position));
            time.setText(timeList.get(position));

            return convertView;
        }
    }


}
