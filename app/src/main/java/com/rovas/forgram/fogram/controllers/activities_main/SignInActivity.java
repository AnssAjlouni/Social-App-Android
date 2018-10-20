package com.rovas.forgram.fogram.controllers.activities_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.helper.LoadHelper;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Mohamed El Sayed
 */
public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static int TIME_OUT = 3000; //Time to launch the another activity
    //Firebase Objects
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;

    //Layout_Views
    private EditText mEmailField;
    private EditText mPasswordField;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //firebase Objects Decleration
        UserWiazrd.getInstance().dispose();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        Button mSignInButton = findViewById(R.id.button_sign_in);
        Button mRegButton = findViewById(R.id.Login_reg_btn);//forward to Reg_Activity
        mRegButton.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    senttoreg();//Intent
                }
            }
        );

        // Click listeners
        mSignInButton.setOnClickListener(this);
    }
    private void senttoreg() {
        Intent mainIntent = new Intent( SignInActivity.this ,SignupActivity.class );
        startActivity(mainIntent);
        finish();//unable to return
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");//Log Details
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());//Log Notification Successful


                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());//Database update data >> Auth // users database
                        } else {
                            hideProgressDialog();//hide
                            Toast.makeText(SignInActivity.this, getString(R.string.signin_failed),//Failed Toast
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Go to MainActivity

                final String device_token = FirebaseInstanceId.getInstance().getToken();//Device Token For Each Device
                String current_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();//Current_user_id

                Map<String , Object > tokenMap = new HashMap<>();//Map For put the data
                tokenMap.put("token_id" , device_token);
                fStore.collection("users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        LoadHelper.Load();
                            hideProgressDialog();//hide
                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            finish();//unable to return
                    }
                });
    }


    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(getString(R.string.required));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(getString(R.string.required));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn();
        }
    }
}
