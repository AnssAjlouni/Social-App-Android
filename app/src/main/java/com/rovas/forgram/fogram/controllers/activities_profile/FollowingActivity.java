package com.rovas.forgram.fogram.controllers.activities_profile;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Synchronizer;
import com.rovas.forgram.fogram.views.FollowingRecyclerViewAdapter;
import com.rovas.forgram.fogram.models.Following;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class FollowingActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FollowingActivity";
    private List<Following> followingList;
    private RecyclerView recyclerView;
    private FollowingRecyclerViewAdapter followingRecyclerViewAdapter;

    private FirebaseAuth mAuth;
    private String User_id;
    private FirebaseFirestore fStore;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_following);

        mAuth = FirebaseAuth.getInstance();
        User_id = getIntent().getStringExtra("user_id");
        fStore = FirebaseFirestore.getInstance();
        //user_notification = databaseReference.child(User_id).child("Notifications");
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_following);
        followingList = new ArrayList<>();

        //followingRecyclerViewAdapter = new FollowingRecyclerViewAdapter(followingList);
        followingRecyclerViewAdapter = new FollowingRecyclerViewAdapter(followingList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(followingRecyclerViewAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);

    }
    @Override
    public void onStart() {
        super.onStart();
        getData();

    }

    @Override
    public void onRefresh() {
        getData();
    }
    public void getData(){
        try {
            swipeRefreshLayout.setRefreshing(true);
            all_Followers();
            //all_Followers_Sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void all_Followers_Sync() {
        followingList.clear();
        Synchronizer synchronizer = new Synchronizer(User_id);
        followingList.addAll( synchronizer.following_connect());
        followingRecyclerViewAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void all_Followers() {
        followingList.clear();
        fStore.collection("users/" + User_id + "/Following")
                .addSnapshotListener(FollowingActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String followingid = doc.getDocument().getId();
                                    Following following = doc.getDocument().toObject(Following.class).withid(followingid);
                                    followingList.add(following);
                                    followingRecyclerViewAdapter.notifyDataSetChanged();


                                }
                            }

                        }
                    }
                });
        Log.d(TAG, "all_Followers: " + followingList.size());
        swipeRefreshLayout.setRefreshing(false);
    }
}
