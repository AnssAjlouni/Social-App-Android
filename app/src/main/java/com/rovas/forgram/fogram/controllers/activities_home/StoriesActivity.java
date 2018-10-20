package com.rovas.forgram.fogram.controllers.activities_home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.models.StoriesPost;
import com.rovas.forgram.fogram.views.SProgressViewAdapter;
import com.rovas.forgram.fogram.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class StoriesActivity extends AppCompatActivity {
    private static final String TAG = "StoriesActivity";

    private List<StoriesPost> stories_list;
    private SProgressViewAdapter sRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private String stories_post_id;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {// Alt + Insert To Open Fast Insert anything
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_stories_change);

        Log.d(TAG, "onCreate: started.");

        //
            /*
            storiesProgressView = (StoriesProgressView) findViewById(R.id.stories_progress);
            storiesProgressView.setStoriesCount(stories_count);
            storiesProgressView.setStoriesCountWithDurations(durations);
            storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
                @Override
                public void onNext() {
                    imageView.setImageResource(resources[++counter]);
                }

                @Override
                public void onPrev() {
                    if ((counter - 1) < 0) return;
                    imageView.setImageResource(resources[--counter]);
                }

                @Override
                public void onComplete() {
                    Toast.makeText(StoriesActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    Intent home_intent = new Intent(StoriesActivity.this, HomeActivity.class);//ACTIVITY_NUM = 0
                    startActivity(home_intent);//content because the Class
                }
            });
            storiesProgressView.startStories();
            imageView = (ImageView) findViewById(R.id.image_stories);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storiesProgressView.skip();
                }
            });
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pressTime = System.currentTimeMillis();
                            storiesProgressView.pause();
                            return false;
                        case MotionEvent.ACTION_UP:
                            long now = System.currentTimeMillis();
                            storiesProgressView.resume();
                            return limit < now - pressTime;
                    }
                    return false;
                }
            });
            imageView.setImageResource(resources[counter]);

        //
        */
        stories_post_id = getIntent().getStringExtra("stories_post_id");
        stories_list = new ArrayList<>();
        recyclerView = findViewById(R.id.stories_recycleriew);
        sRecyclerViewAdapter = new SProgressViewAdapter(stories_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(StoriesActivity.this, LinearLayoutManager.HORIZONTAL, false);
        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(sRecyclerViewAdapter);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            fStore = FirebaseFirestore.getInstance();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);



                }
            });
            //mUserDatabase.keepSynced(true);

            Query f_query = fStore.collection("Stories/" + stories_post_id + "/Photos" ).orderBy("time_stamp" , Query.Direction.ASCENDING);
            //.orderBy("time_stamp" ,Query.Direction.DESCENDING);
            f_query.addSnapshotListener(StoriesActivity.this ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String sPostId = doc.getDocument().getId();
                            StoriesPost sPost = doc.getDocument().toObject(StoriesPost.class).withid(sPostId);
                            stories_list.add(sPost);
                            sRecyclerViewAdapter.notifyDataSetChanged();
                            //Toast.makeText(getContext(), "Passed", Toast.LENGTH_SHORT).show();

                        }
                    }

                }
            });


        }


    }
    /*
    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }
    */

}
