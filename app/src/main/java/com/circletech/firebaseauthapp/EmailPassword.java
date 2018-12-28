package com.circletech.firebaseauthapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class EmailPassword extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPAss;
    private TextView loggedDetails;
    private Button mLogOut;
    private boolean mLogged;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_email_password);

        mLogged = false;
        loggedDetails = findViewById(R.id.tv_details);
         mEmail = findViewById(R.id.et_email);
         mPAss = findViewById(R.id.et_password);

        Button mSignUp = findViewById(R.id.bt_signup);
        Button mSignIn =findViewById(R.id.bt_continue);
         mLogOut = findViewById(R.id.bt_logout);

        mFirebaseAuth = FirebaseAuth.getInstance();


        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                final String email = mEmail.getText().toString();
                final String pass = mPAss.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()) {
                    if(!mLogged)
                        signInMethod(email, pass);
                    else{
                        Toast.makeText(EmailPassword.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (email.isEmpty())
                        mEmail.setError("Enter valid Email");
                    if (pass.isEmpty()) {
                        mPAss.setError("Enter Password");
                    }
                }

            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                final String email = mEmail.getText().toString();
                final String pass = mPAss.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()) {
                    if(!mLogged)
                    signUpMethod(email, pass);
                    else{
                        Toast.makeText(EmailPassword.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (email.isEmpty())
                        mEmail.setError("Enter valid Email");
                    if (pass.isEmpty()) {
                        mPAss.setError("Enter Password");
                    }
                }
            }
        });
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                logOutMethod(pView);
            }
        });
    }
    private void signInMethod(String pEmail,String pPass){
        mFirebaseAuth.signInWithEmailAndPassword(pEmail,pPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> pTask) {
                        if (pTask.isSuccessful()) {
                            mLogged=true;
                            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            updateUI(mFirebaseUser);

                        } else {
                            mLogged = false;
                            updateUI(null);
                        }
                    }
                });

    }

    private void logOutMethod(View pView) {
        if (!mLogged) {
            pView.setEnabled(false);
            updateUI(null);
        } else {
            mFirebaseAuth.signOut();
            mLogged = false;
            updateUI(null);
            pView.setEnabled(false);
        }
    }

    private void signUpMethod(String pEmail, String pPass) {


        mFirebaseAuth.createUserWithEmailAndPassword(pEmail, pPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> pTask) {
                        if (pTask.isSuccessful()) {
                            mLogged=true;
                            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            updateUI(mFirebaseUser);

                        } else {
                            mLogged = false;
                            updateUI(null);
                        }
                    }
                });

    }


    private void updateUI(FirebaseUser pFirebaseUser) {
        if (mLogged) {
            String vStringBuilder = "Logged In\n" +
                    pFirebaseUser.getEmail() + "\n" +
                    pFirebaseUser.getUid();
            loggedDetails.setText(vStringBuilder);
            mLogOut.setEnabled(true);
            mLogOut.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            loggedDetails.setText(" Logged Out");
            mLogOut.setEnabled(false);
            mLogOut.setBackgroundColor(Color.parseColor("#808080"));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser vUser = mFirebaseAuth.getCurrentUser();
        if (vUser != null) {
            mLogged = true;
            updateUI(vUser);
        }
        else{
            mLogged=false;
            updateUI(null);
        }
    }

}
