package com.ua.yuriihrechka.writeme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {


    private Button updateAccountButton;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserId;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinds);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        userName.setVisibility(View.INVISIBLE);

        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {

        mRootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists())
                        && (dataSnapshot.hasChild("name"))
                        && (dataSnapshot.hasChild("image"))){

                            String retriveUserName = dataSnapshot.child("name").getValue().toString();
                            String retriveStatus = dataSnapshot.child("status").getValue().toString();
                            String retriveProfileImage = dataSnapshot.child("image").getValue().toString();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveStatus);

                        }else if((dataSnapshot.exists())
                                && (dataSnapshot.hasChild("name"))){
                            String retriveUserName = dataSnapshot.child("name").getValue().toString();
                            String retriveStatus = dataSnapshot.child("status").getValue().toString();

                            userName.setText(retriveUserName);
                            userStatus.setText(retriveStatus);
                        }else {
                            Toast.makeText(SettingsActivity.this, "Set & update...", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void updateSettings() {

        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Write user name...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)){
            Toast.makeText(this, "Write status...", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);

            mRootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Successfuly", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

        }


    }

    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void initializeFields() {
        updateAccountButton = (Button) findViewById(R.id.update_settings_button);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_image);

    }
}