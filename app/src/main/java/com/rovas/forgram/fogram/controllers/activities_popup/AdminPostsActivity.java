package com.rovas.forgram.fogram.controllers.activities_popup;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.views.BlogRecyclyerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminPostsActivity extends AppCompatActivity implements BlogRecyclyerAdapter.PublishClickListener {
    private static final String TAG = "AdminPostsActivity";
    // Store a member variable for the listener
    DocumentSnapshot lastVisible;
    private boolean isLastItemReached = false;
    private Boolean isScrolling = false;
    //models
    private List<BlogPost> blog_list;
    //RecycleAdapters
    private BlogRecyclyerAdapter blogRecyclyerAdapter;
    //RecycleViews
    private RecyclerView blog_list_view;
    //FireBase
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    //
    private LinearLayoutManager mManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_posts);
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        //Recent_posts_Lists
        blog_list = new ArrayList<>();
        //
        blog_list_view = findViewById(R.id.blog_list_view_admin);
        blog_list_view.setHasFixedSize(true);
        //
        blogRecyclyerAdapter = new BlogRecyclyerAdapter(blog_list , this);
        //blog_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        //

        mManager = new LinearLayoutManager(this);
        //mManager.setReverseLayout(true);
        //mManager.setStackFromEnd(true);
        blog_list_view.setLayoutManager(mManager);
        //
        //
        blog_list_view.setAdapter(blogRecyclyerAdapter);
        getData();

    }


    @Override
    public void onPublishClicked() {

    }

    public void getAllPosts()
    {
        blog_list.clear();
        isLastItemReached = false;
        isScrolling = false;
        if(mAuth.getCurrentUser() != null)
        {
            /*
            blog_list_view.addOnScrollListener(new EndlessRecyclerViewScrollListener(mManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page , totalItemsCount);
                    //Log.d(TAG, "onLoadMore: Scrolling");
                }

            });
            //mUserDatabase.keepSynced(true);
            */

            Query f_query = fStore.collection("Admins").orderBy("time_stamp" ,Query.Direction.DESCENDING).limit(10) ;
            f_query.addSnapshotListener(AdminPostsActivity.this ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            final String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withid(blogPostId);
                            blog_list.add(blogPost);
                            lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);
                            String blogUserID = doc.getDocument().getString("user_id");
                            fStore.collection("users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(task.getResult().exists()) {
                                            //String username = task.getResult().getString("username");
                                            String thumb_image = task.getResult().getString("thumb_image");
                                            HashMap<String, Object> userMap_ = new HashMap<>();
                                            //userMap_.put("name", username);
                                            userMap_.put("thumb_image", thumb_image);
                                            fStore.collection("Admins").document(blogPostId).update(userMap_);
                                            Log.d(TAG, "HomePage:  1st Page Loaded");
                                        }
                                    }
                                }
                            });
                            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                    {
                                        isScrolling = true;
                                    }
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    //Cloud Firestore Pagination
                                    //https://www.youtube.com/watch?v=KdgKvLll07s
                                    int firstVisibleItem = mManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = mManager.getChildCount();
                                    int totalItemCount = mManager.getItemCount();
                                    if(isScrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemReached)
                                    {
                                        isScrolling = false;
                                        loadNextDataFromApi();

                                    }
                                }
                            };

                            blogRecyclyerAdapter.notifyDataSetChanged();
                            blog_list_view.addOnScrollListener(onScrollListener);

                        }
                    }


                }
            });
            Log.d(TAG, "getAllPosts: "+ blog_list.size());

        }


    }

    public void getData(){
        try {
            getAllPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadNextDataFromApi() {
        Query f_query = fStore.collection("Admins").orderBy("time_stamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10);
        f_query.addSnapshotListener(AdminPostsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        final String blogPostId = doc.getDocument().getId();
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withid(blogPostId);
                        blog_list.add(blogPost);
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);
                        String blogUserID = doc.getDocument().getString("user_id");
                        fStore.collection("users").document(blogUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        //String username = task.getResult().getString("username");
                                        String thumb_image = task.getResult().getString("thumb_image");
                                        HashMap<String, Object> userMap_ = new HashMap<>();
                                        //userMap_.put("name", username);
                                        userMap_.put("thumb_image", thumb_image);
                                        fStore.collection("Admins").document(blogPostId).update(userMap_);
                                    }


                                }
                                if (documentSnapshots.size() < 10) {

                                    isLastItemReached = true;
                                }

                            }
                        });

                        blogRecyclyerAdapter.notifyDataSetChanged();

                    }
                }


            }
        });
    }
}
