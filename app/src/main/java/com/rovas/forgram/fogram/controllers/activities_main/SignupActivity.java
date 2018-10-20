package com.rovas.forgram.fogram.controllers.activities_main;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.base.BaseActivity;
import com.rovas.forgram.fogram.enums.AccountState;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.User;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Random;
/**
 * Created by Mohamed El Sayed
 */
public class SignupActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    //Firebase Objects
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    //Layout_Views
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mSignUpButton;
    private Button mLogButton;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mSignUpButton = findViewById(R.id.button_sign_up);
        mLogButton = (Button) findViewById(R.id.reg_login_btn);
        mLogButton.setOnClickListener( new View.OnClickListener()//forward to Log_Activity
            {
                @Override
                public void onClick(View view) {
                    senttologin();
                }
            }
        );

        // Click listeners
        //mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }
    private void senttologin() {
        Intent mainIntent = new Intent( SignupActivity.this ,SignInActivity.class );
        startActivity(mainIntent);
        finish();
    }
    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }


    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            hideProgressDialog();//hide
                            Toast.makeText(SignupActivity.this, getString(R.string.signup_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(final FirebaseUser user) {
        //generic "in between two numbers"
        Random r = new Random();
        int Low = 10;
        int High = 100;
        final int random_number = r.nextInt(High-Low) + Low;
        final String username = usernameFromEmail(user.getEmail());//get username value
        HashMap<String, String> userMap_ = new HashMap<>();
        userMap_.put("user_id", user.getUid());
        userMap_.put("username", username);
        fStore.collection("users").document(user.getUid()).set(userMap_);
        com.google.firebase.firestore.Query mQuery = fStore.collection("users")
                .whereEqualTo("username", username);//Check username

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()){//if username exists >> There's a problem

                            String user_name_ex = username + random_number;//username + random_number to solve the problem
                            // Write new user
                            writeNewUser(user.getUid(), user_name_ex, user.getEmail());
                            hideProgressDialog();
                            // Go to MainActivity
                            startActivity(new Intent(SignupActivity.this, SetupActivity.class));
                            finish();
                        }
                        else
                        {
                            // Write new user
                            writeNewUser(user.getUid(), username, user.getEmail());
                            hideProgressDialog();
                            // Go to MainActivity
                            startActivity(new Intent(SignupActivity.this, SetupActivity.class));
                            finish();
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    private boolean userNameExistsAlready(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());
        com.google.firebase.firestore.Query mQuery = fStore.collection("users")
                .whereEqualTo("username", username);

       mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if (task.isSuccessful()) {
                   for (DocumentSnapshot document : task.getResult()) {
                       if (document.exists()){
                           Toast.makeText(SignupActivity.this, "Username Exists Already!", Toast.LENGTH_SHORT).show();
                       }
                   }
               } else {
                   Log.d(TAG, "Error getting documents: ", task.getException());
               }
           }
       });
        return false;
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

    // [START basic_write]
    private void writeNewUser(final String userId, final String name, final String email) {
        //User user = new User(name, email);
        User user = new User(userId ,01277637646 , email , name);
        final String device_token = FirebaseInstanceId.getInstance().getToken();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        HashMap<String, Object> userMap_ = new HashMap<>();
        // ----------------------------------------
        userMap_.put("user_id", userId);
        userMap_.put("username", name);
        userMap_.put("email" , email);
        userMap_.put("online",  timestamp.getTime());
        userMap_.put("token_id" , device_token);
        userMap_.put("user_ip" , ip);
        userMap_.put("role" , 0);//0
        // ----------------------------------------
        userMap_.put("following", 0);
        userMap_.put("followers", 0);
        userMap_.put("posts", 0);
        userMap_.put("name", "");
        userMap_.put("created_date" ,timestamp.getTime());
        userMap_.put("brith_date", "");
        userMap_.put("status", "i'm available.");
        userMap_.put("image", "none");
        userMap_.put("thumb_image", "none");
        fStore.collection("users").document(userId).set(userMap_);
        UserWiazrd.getInstance().getTempUser().setUsername(name);
        UserWiazrd.getInstance().getTempUser().setStatus("i'm available.");
        UserWiazrd.getInstance().getTempUser().setToken_id(device_token);


    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
       if (i == R.id.button_sign_up) {
            signUp();
        }
    }
}
