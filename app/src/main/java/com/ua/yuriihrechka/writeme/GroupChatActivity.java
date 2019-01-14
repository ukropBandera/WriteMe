package com.ua.yuriihrechka.writeme;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView scrollView;
    private TextView displayTextMessage;

    private String currentGroupName;

    private String currentUserID;
    private String currentUserName;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupNameRef, groupMesKeyRef;

    private String currentDate, currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        //currentUserName = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);



        init();
        getUserInfo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDB();

                userMessageInput.setText("");
            }
        });
    }

    private void saveMessageInfoToDB() {

        String message = userMessageInput.getText().toString();
        String messageKey = groupNameRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "message empty", Toast.LENGTH_SHORT).show();
        }else {
            Calendar curDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(curDate.getTime());

            Calendar curTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm:ss");
            currentTime = currentTimeFormat.format(curTime.getTime());


            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMesKeyRef = groupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);
            groupMesKeyRef.updateChildren(messageInfoMap);

        }

    }

    private void getUserInfo() {

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUserName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void init() {

        toolbar = (Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton = (ImageButton)findViewById(R.id.send_message_button);
        userMessageInput = (EditText)findViewById(R.id.input_group_message);
        scrollView = (ScrollView)findViewById(R.id.my_scroll_view);
        displayTextMessage = (TextView)findViewById(R.id.group_chat_text_display);

    }
}
