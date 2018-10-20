package com.rovas.forgram.fogram.controllers.activities_popup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rovas.forgram.fogram.controllers.activities_notification.PhotoForward;
import com.rovas.forgram.fogram.controllers.activities_notification.PhotoTextForward;
import com.rovas.forgram.fogram.controllers.activities_notification.TCommentsActivity;
import com.rovas.forgram.fogram.controllers.activities_notification.TCommentsPhotoActivity;
import com.rovas.forgram.fogram.controllers.activities_notification.TweetsForward;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.controllers.activities_main.HomeActivity;
/**
 * Created by Mohamed El Sayed
 */
public class NotificationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String current_user_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);



    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        current_user_ID = mAuth.getCurrentUser().getUid();
        String message = getIntent().getStringExtra("message");
        String from  =   getIntent().getStringExtra("from_user_id");
        String post = getIntent().getStringExtra("post");
        //String type = getIntent().getStringExtra("type");
        String forward  =   getIntent().getStringExtra("forward");
        //

        if(!current_user_ID.equals(from))
        {
            switch (post) {
                case "Tweet": {
                    Intent Intent = new Intent(NotificationActivity.this, TCommentsActivity.class);
                    Intent.putExtra("from", from);
                    //Intent.putExtra("thumb_image", thumb_image);
                    //Intent.putExtra("time_stamp", time);
                    //Intent.putExtra("username", username);
                    Intent.putExtra("forward", forward);
                    Intent.putExtra("message", message);
                    Intent.putExtra("post", post);
                    startActivity(Intent);
                    finish();//Don't Return AnyMore TO the last page

                    break;
                }
                case "photo_tweet": {
                    Intent Intent = new Intent(NotificationActivity.this, PhotoTextForward.class);
                    Intent.putExtra("from", from);
                    //Intent.putExtra("thumb_image", thumb_image);
                    //Intent.putExtra("time_stamp", time);
                    //Intent.putExtra("username", username);
                    Intent.putExtra("forward", forward);
                    Intent.putExtra("message", message);
                    Intent.putExtra("post", post);
                    startActivity(Intent);
                    finish();//Don't Return AnyMore TO the last page

                    break;
                }
                case "Photo": {
                    Intent Intent = new Intent(NotificationActivity.this, TCommentsPhotoActivity.class);
                    Intent.putExtra("from", from);
                    //Intent.putExtra("thumb_image", thumb_image);
                    //Intent.putExtra("time_stamp", time);
                    //Intent.putExtra("username", username);
                    Intent.putExtra("forward", forward);
                    Intent.putExtra("message", message);
                    Intent.putExtra("post", post);
                    startActivity(Intent);
                    finish();//Don't Return AnyMore TO the last page

                    break;
                }
                default:
                {
                    Toast.makeText(this, "Type not Found", Toast.LENGTH_SHORT).show();
                    break;
                }
            }


        }
        else
        {
            Intent Intent = new Intent(NotificationActivity.this, HomeActivity.class);
            startActivity(Intent);
            finish();//Don't Return AnyMore TO the last page
        }

    }

}
