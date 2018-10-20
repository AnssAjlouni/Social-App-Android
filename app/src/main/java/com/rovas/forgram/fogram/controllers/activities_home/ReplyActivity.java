package com.rovas.forgram.fogram.controllers.activities_home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.Reply;
import com.rovas.forgram.fogram.models.User;
import com.rovas.forgram.fogram.views.ReplyRecyclerAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Mohamed El Sayed
 */
public class ReplyActivity extends AppCompatActivity implements TextWatcher , View.OnClickListener{
    //=========== RecycleView =============
    private ReplyRecyclerAdapter replyRecyclerAdapter;
    private List<Reply> replyList;
    private List<User> user_list;
    private List<BlogPost> blog_list;
    //=========== FireBase =============
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    //=========== Others =============
    private String blog_post_id;
    private String comment_post_id;
    //=========== VIews =============
    private EditText commentEditText;
    @Nullable
    private ScrollView scrollView;
    private Button sendButton;
    private ProgressBar commentsProgressBar;
    private RecyclerView commentsRecyclerView;
    private TextView warningCommentsTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        //=========== FireBase =============
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        //=========== ExtraIntent =============
        blog_post_id = getIntent().getStringExtra("blog_post_id");
        comment_post_id = getIntent().getStringExtra("comment_id");

        initViews();
        setViews();
        sharedPreferences();
        updateRecycleView();
        loadReplys();
        sendButton.setOnClickListener(this);
        commentEditText.addTextChangedListener(this);
    }

    private void loadReplys() {
        //mUserDatabase.keepSynced(true);
        Query f_query = firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments/"+comment_post_id+"/Reply").orderBy("timestamp", Query.Direction.ASCENDING);

        f_query.addSnapshotListener(ReplyActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            final String replyid = doc.getDocument().getId();
                            Reply comments = doc.getDocument().toObject(Reply.class).withid(replyid);
                            replyList.add(comments);
                            String blogUserID = doc.getDocument().getString("user_id");
                            firebaseFirestore.collection("users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            String username = task.getResult().getString("username");
                                            String thumb_image = task.getResult().getString("thumb_image");
                                            HashMap<String, Object> userMap_ = new HashMap<>();
                                            userMap_.put("username", username);
                                            userMap_.put("thumb_image", thumb_image);
                                            firebaseFirestore.collection("Posts").document(blog_post_id)
                                                    .collection("Comments").document(comment_post_id).collection("Reply").document(replyid).update(userMap_);
                                        }


                                    }
                                }
                            });
                            replyRecyclerAdapter.notifyDataSetChanged();


                        }
                    }

                }

            }
        });
    }

    private void updateRecycleView() {
        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();
        //RecyclerView Firebase List
        replyList = new ArrayList<>();
        replyRecyclerAdapter = new ReplyRecyclerAdapter(replyList , user_list , blog_list );
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(replyRecyclerAdapter);
    }

    private void sharedPreferences() {
        SharedPreferences prefs_c = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor_c = prefs_c.edit();
        editor_c.putString("comment_post_id", comment_post_id); //InputString: from the EditText
        editor_c.commit();
        SharedPreferences prefs_ = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor_ = prefs_.edit();
        editor_.putString("blog_post_id", blog_post_id); //InputString: from the EditText
        editor_.commit();
    }

    private void setViews() {

    }

    private void initViews() {
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        scrollView = findViewById(R.id.scrollView);
        commentEditText = findViewById(R.id.commentEditText);
        commentsProgressBar = findViewById(R.id.commentsProgressBar);
        warningCommentsTextView = findViewById(R.id.warningCommentsTextView);
        sendButton = findViewById(R.id.sendButton);
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        sendButton.setEnabled(charSequence.toString().trim().length() > 0);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.sendButton:
            {
                sendComment();
                break;
            }
            default:
                break;
        }
    }

    private void sendComment() {
        String comment_message = commentEditText.getText().toString();

        if(!TextUtils.isEmpty(comment_message)) {
            commentEditText.setText("");
            firebaseFirestore.collection("users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            final String username = task.getResult().getString("username");
                            final String thumb_image = task.getResult().getString("thumb_image");

                            final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                            Map<String, Object> commentsMap_ = new HashMap<>();
                            commentsMap_.put("message", comment_message);
                            commentsMap_.put("user_id", current_user_id);
                            commentsMap_.put("timestamp", timestamp.getTime());
                            commentsMap_.put("username", username);
                            commentsMap_.put("thumb_image", thumb_image);
                            firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments/" + comment_post_id + "/Reply").add(commentsMap_).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
