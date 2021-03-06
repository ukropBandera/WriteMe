package com.ua.yuriihrechka.writeme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, phoneLoginBtn;
    private EditText userEmail, userPassword;
    private TextView needNewAccountLink;

    private ProgressDialog loadingBar;



    // fb
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference dbUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        InitialzeFields();

        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        phoneLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPhone = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(intentPhone);
            }
        });

    }

    private void AllowUserToLogin() {

        String email = userEmail.getText().toString();
        String pass = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(), "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Sing in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                dbUserRef.child(currentUserID)
                                        .child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){

                                                    sendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });




                            }else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: "+ message, Toast.LENGTH_SHORT).show();
                            }

                            loadingBar.dismiss();

                        }
                    });
        }


    }

    private void InitialzeFields() {

        loginButton = (Button)findViewById(R.id.login_button);
        userEmail = (EditText)findViewById(R.id.login_email);
        userPassword = (EditText)findViewById(R.id.login_password);
        needNewAccountLink = (TextView)findViewById(R.id.need_new_account_link);
        phoneLoginBtn = (Button)findViewById(R.id.phone_login_button);
        loadingBar = new ProgressDialog(this);
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null){
            sendUserToMainActivity();
        }
    }*/

    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {

        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
