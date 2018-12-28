package com.circletech.firebaseauthapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordLess extends AppCompatActivity implements View.OnClickListener {

    private static final String EMAIL_KEY = "email_key_sent";
    private EditText mEmail;
    private Button mSendLink, mSignIn, mSignOut;
    private FirebaseAuth mFirebaseAuth;
    private TextView mDetailsUser;
    private String mPendingEmail;
    private String mEmailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_link);

        //views from layout
        mEmail = findViewById(R.id.et_email);
        mSendLink = findViewById(R.id.bt_send_link);
        mSignIn = findViewById(R.id.bt_continue);
        mSignOut = findViewById(R.id.bt_logout);
        mDetailsUser = findViewById(R.id.tv_details);

        //setListeners
        mSendLink.setOnClickListener(this);
        mSignOut.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

        //firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        //get the Entered Email Address before to verify
        if (savedInstanceState != null) {
            mPendingEmail = savedInstanceState.getString(EMAIL_KEY, null);
            mEmail.setText(mPendingEmail);
        }
        checkIntentDetail(getIntent());
    }

    private void checkIntentDetail(@Nullable Intent pIntent) {
        if (intentHasEmailLink(pIntent)) {
            mEmailLink = pIntent.getData().toString();
            mDetailsUser.setText("Email Link Found");
            mSendLink.setEnabled(false);
            mSignIn.setEnabled(true);
        } else {
            mDetailsUser.setText("Email Link not found");
            mSendLink.setEnabled(true);
            mSignIn.setEnabled(false);
        }
    }

    private boolean intentHasEmailLink(@Nullable Intent intent) {
        if (intent != null && intent.getData() != null) {
            String intentData = intent.getData().toString();
            if (mFirebaseAuth.isSignInWithEmailLink(intentData)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntentDetail(intent);
    }

    //store the Email Entered in the editext for when we return back from verification
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL_KEY, mEmail.getText().toString());
    }

    //check if already logged in or not
    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mFirebaseAuth.getCurrentUser());
    }

    private void sendEmailLink(final String pEmailAddress) {

        ActionCodeSettings vActionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(getPackageName(), false, null)
                .setHandleCodeInApp(true)
                .setUrl("https://auth.example.com/emailSignInLink")
                .build();
        mFirebaseAuth.sendSignInLinkToEmail(pEmailAddress, vActionCodeSettings)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> pTask) {
                        if (pTask.isSuccessful()) {

                            showSnackbar("Sign-in link sent!");

                            mPendingEmail = pEmailAddress;
                            mDetailsUser.setText(R.string.status_email_sent);
                        } else {
                            Exception e = pTask.getException();

                            showSnackbar("Failed to send link.");

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                mEmail.setError("Invalid email address.");
                            }
                        }
                    }
                });
    }

    private void signInEmailLink(String pEmail, String pEmailLink) {
        Toast.makeText(this, ""+pEmailLink, Toast.LENGTH_SHORT).show();
        mFirebaseAuth.signInWithEmailLink(pEmail, pEmailLink)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> pTask) {
                        if (pTask.isSuccessful()) {
                            mEmail.setText(null);
                            updateUI(pTask.getResult().getUser());
                        } else {
                            updateUI(null);

                            if (pTask.getException() instanceof FirebaseAuthActionCodeException) {
                                showSnackbar("Invalid or expired sign-in link.");
                            }
                        }
                    }
                });

    }


    private void updateUI(FirebaseUser pFirebaseUser) {
        if (pFirebaseUser != null) {
            mDetailsUser.setText(getString(R.string.passwordless_status_fmt,
                    pFirebaseUser.getEmail(), pFirebaseUser.isEmailVerified()));

           mSendLink.setEnabled(false);
           mSignIn.setEnabled(false);
           mSignOut.setEnabled(true);
           mSignOut.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mSendLink.setEnabled(true);
            mSignIn.setEnabled(true);
            mSignOut.setEnabled(false);
            mSignOut.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public void onClick(View pView) {
        switch (pView.getId()) {
            // sign IN Email
            case R.id.bt_continue:
                onSignInClicked();
                break;
            //Send Link  to EMail
            case R.id.bt_send_link:
                onSendLinkClicked();
                break;
            //logout from email
            case R.id.bt_logout:
                onSignOutClicked();
                break;
            default:
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSendLinkClicked() {
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email must not be empty.");
            return;
        }

        sendEmailLink(email);
    }

    private void onSignInClicked() {
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email must not be empty.");
            return;
        }

        signInEmailLink(email, mEmailLink);
    }

    private void onSignOutClicked() {
        mFirebaseAuth.signOut();

        updateUI(null);
        mDetailsUser.setText(R.string.status_email_not_sent);
    }

    private void showSnackbar(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
