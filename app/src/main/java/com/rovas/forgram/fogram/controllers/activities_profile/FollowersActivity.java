package com.rovas.forgram.fogram.controllers.activities_profile;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.views.FollowersRecyclerViewAdapter;
import com.rovas.forgram.fogram.models.Followers;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class FollowersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List<Followers> followersList;
    private RecyclerView recyclerView;
    private FollowersRecyclerViewAdapter followersRecyclerViewAdapter;

    private FirebaseAuth mAuth;
    private String User_id;
    private FirebaseFirestore fStore;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_followers);

        mAuth = FirebaseAuth.getInstance();
        User_id = getIntent().getStringExtra("user_id");
        fStore = FirebaseFirestore.getInstance();
        //user_notification = databaseReference.child(User_id).child("Notifications");

        recyclerView = (RecyclerView) findViewById(R.id.recycleView_followers);
        followersList = new ArrayList<>();

        followersRecyclerViewAdapter = new FollowersRecyclerViewAdapter(followersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(followersRecyclerViewAdapter);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void all_Followers() {
        followersList.clear();
        fStore.collection("users/" + User_id + "/Followers")
                .addSnapshotListener(FollowersActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String followersid = doc.getDocument().getId();
                                    Followers followers = doc.getDocument().toObject(Followers.class).withid(followersid);
                                    followersList.add(followers);
                                    followersRecyclerViewAdapter.notifyDataSetChanged();


                                }
                            }

                        }
                    }
                });
        swipeRefreshLayout.setRefreshing(false);
    }
}
