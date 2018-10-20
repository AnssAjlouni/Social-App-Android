package com.rovas.forgram.fogram.controllers.activities_notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.Utils.UI.PostUI;
import com.rovas.forgram.fogram.controllers.activities_popup.vProfileActivity;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.Comments;
import com.rovas.forgram.fogram.models.User;
import com.rovas.forgram.fogram.views.CommentsRecyclerAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohamed El Sayed
 */
public class TCommentsActivity extends AppCompatActivity implements TextWatcher ,View.OnClickListener {

    //=========== RecycleView =============
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;
    private List<User> user_list;
    private List<BlogPost> blog_list;
    //=========== FireBase =============
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    //=========== VIews =============
    private EditText commentEditText;
    @Nullable
    private ScrollView scrollView;
    private ViewGroup likesContainer;
    private ImageView likesImageView;
    private TextView commentsLabel;
    private TextView likeCounterTextView;
    private TextView commentsCountTextView;
    private TextView watcherCounterTextView;
    private TextView authorTextView;
    private TextView dateTextView;
    private ImageView authorImageView;
    private TextView titleTextView;
    private TextView descriptionEditText;
    private ProgressBar commentsProgressBar;
    private RecyclerView commentsRecyclerView;
    private TextView warningCommentsTextView;
    private Button sendButton;
    //=========== Toolbar =============
    private MenuItem complainActionMenuItem;
    private MenuItem editActionMenuItem;
    private MenuItem deleteActionMenuItem;
    //=========== Others =============
    private Toolbar commentToolbar;
    private String blog_post_id;
    private String thumb_image , username , post_type ;
    private long time_stamp;
    private String owner_id;

    //private BlogPost blogPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        initViews();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        //blogPost = (BlogPost) getIntent().getExtras().getSerializable(PostUI.POST_COMMENT_BUNDLE);
        blog_post_id = getIntent().getStringExtra("forward");
        owner_id = getIntent().getStringExtra("from");
        thumb_image = getIntent().getStringExtra("thumb_image");
        time_stamp = getIntent().getLongExtra("time_stamp" , 0);
        username = getIntent().getStringExtra("username");
        post_type = getIntent().getStringExtra("post");
        setViews();
        //
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("blog_post_id", blog_post_id); //InputString: from the EditText
        editor.commit();
        //
        updateRecycleView();
        loadComments();

        sendButton.setOnClickListener(this);
        authorImageView.setOnClickListener(this);//View Profile
        authorTextView.setOnClickListener(this);//View Profile
        commentEditText.addTextChangedListener(this);
    }

    private void setViews() {
        //UserImage
        Glide.with(this).load(thumb_image).into(authorImageView);
        //Glide.with(this).load(blogPost.getImage_url()).into(postImageView);
        //UserName
        authorTextView.setText(username);
        //Descrption
        firebaseFirestore.collection("Posts").document(blog_post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        String text_post = task.getResult().getString("text_post");
                        descriptionEditText.setText(text_post);
                    }
                }
            }
        });
        //PostTime
        String lastSeenTime = GetTimeAgo.getTimeAgo(time_stamp, this);
        dateTextView.setText(lastSeenTime);
        //LikesCount
        //likeCounterTextView.setText(blogPost.getLikes_count());

    }

    private void updateRecycleView() {
        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();
        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsRecyclerAdapter);
        //mUserDatabase.keepSynced(true);
    }

    private void loadComments() {
        if(firebaseAuth.getCurrentUser() != null) {

            commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });
            Query f_query = firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING);
            f_query.addSnapshotListener(TCommentsActivity.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            //
                            final String commentId = doc.getDocument().getId();
                            Comments comments = doc.getDocument().toObject(Comments.class).withid(commentId);
                            commentsList.add(comments);
                            //
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
                                            firebaseFirestore.collection("Posts").document(blog_post_id).collection("Comments").document(commentId).update(userMap_);
                                        }


                                    }
                                }
                            });
                            commentsRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }
    }

    private void initViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        scrollView = findViewById(R.id.scrollView);
        commentsLabel = findViewById(R.id.commentsLabel);
        commentEditText = findViewById(R.id.commentEditText);
        likesContainer = findViewById(R.id.likesContainer);
        likesImageView = findViewById(R.id.likesImageView);
        authorImageView = findViewById(R.id.authorImageView);
        authorTextView = findViewById(R.id.authorTextView);
        likeCounterTextView = findViewById(R.id.likeCounterTextView);
        commentsCountTextView = findViewById(R.id.commentsCountTextView);
        watcherCounterTextView = findViewById(R.id.watcherCounterTextView);
        dateTextView = findViewById(R.id.dateTextView);
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
            case R.id.authorImageView:
            {
                Intent commentIntent = new Intent(TCommentsActivity.this, vProfileActivity.class);
                commentIntent.putExtra("user_id", owner_id);
                startActivity(commentIntent);
                break;
            }
            case R.id.authorTextView:
            {
                Intent commentIntent = new Intent(TCommentsActivity.this, vProfileActivity.class);
                commentIntent.putExtra("user_id", owner_id);
                startActivity(commentIntent);
                break;
            }
            default:
                break;
        }
    }

    private void sendComment() {
        final String comment_message = commentEditText.getText().toString();

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if(!TextUtils.isEmpty(comment_message)) {
            commentEditText.setText("");
            firebaseFirestore.collection("users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            final String username = task.getResult().getString("username");
                            final String thumb_image = task.getResult().getString("thumb_image");
                            Map<String, Object> commentsMap = new HashMap<>();
                            commentsMap.put("message", comment_message);
                            commentsMap.put("user_id", current_user_id);
                            commentsMap.put("timestamp", timestamp.getTime());
                            commentsMap.put("username", username);
                            commentsMap.put("thumb_image", thumb_image);
                            firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                    if (!task.isSuccessful()) {

                                        Toast.makeText(TCommentsActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    } else {

                                        if (!current_user_id.equals(owner_id)) {


                                            firebaseFirestore.collection("users/" + owner_id + "/Notifications/").document(blog_post_id + "!" + username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (!task.getResult().exists()) {
                                                        String message_noti = username + " Commented : " + comment_message;
                                                        Map<String, Object> notificatinMap = new HashMap<>();
                                                        notificatinMap.put("message", message_noti);
                                                        notificatinMap.put("from", current_user_id);
                                                        notificatinMap.put("thumb_image", thumb_image);
                                                        notificatinMap.put("username", username);
                                                        notificatinMap.put("post", post_type);
                                                        notificatinMap.put("type", "comment");
                                                        notificatinMap.put("forward", blog_post_id);
                                                        notificatinMap.put("timestamp", timestamp.getTime());
                                                        firebaseFirestore.collection("users/" + owner_id + "/Notifications/").document(blog_post_id + "!" + username).delete();
                                                        firebaseFirestore.collection("users/" + owner_id + "/Notifications/").document(blog_post_id + "!" + username).set(notificatinMap);

                                                    }
                                                }
                                            });


                                        }

                                    }

                                }
                            });
                        }


                    }
                }
            });
        }
    }
}
