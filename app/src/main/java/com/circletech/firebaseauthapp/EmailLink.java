package com.circletech.firebaseauthapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class EmailLink extends AppCompatActivity {

    private EditText mEmail;
    private Button mSendLink,mSignIn,mSignOut;
    private FirebaseAuth mFirebaseAuth;
    private TextView mDetailsUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_link);

        //views from layout
        mEmail=findViewById(R.id.et_email);
        mSendLink=findViewById(R.id.bt_send_link);
        mSignIn=findViewById(R.id.bt_continue);
        mSignOut=findViewById(R.id.bt_logout);
        mDetailsUser=findViewById(R.id.tv_details);



    }
}
