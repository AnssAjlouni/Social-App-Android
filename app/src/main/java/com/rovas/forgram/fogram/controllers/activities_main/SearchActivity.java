package com.rovas.forgram.fogram.controllers.activities_main;

import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.AnimationUtils;
import com.rovas.forgram.fogram.base.BaseActivityToolbar;
import com.rovas.forgram.fogram.models.User;
import com.rovas.forgram.fogram.views.SearchUserRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class SearchActivity extends BaseActivityToolbar {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private android.support.v7.widget.SearchView searchView;
    private TextView emptyListMessageTextView;
    private ProgressBar progressBar;
    //====================RecyclerView================
    private RecyclerView recyclerView;
    private LinearLayoutManager mManager;
    private SearchUserRecyclerViewAdapter mAdapter;
    private List<User> usersList;

    //====================FireBase================
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //====================FireBase================
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        //====================Toolbar================
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        progressBar = findViewById(R.id.progressBar);
        emptyListMessageTextView = findViewById(R.id.emptyListMessageTextView);
        emptyListMessageTextView.setText(getResources().getString(R.string.empty_user_search_message));
        updateRecycleView();
        showEmptyListLayout();

    }
    private void updateRecycleView() {
        recyclerView = findViewById(R.id.recycler_view);
        usersList = new ArrayList<>();
        mAdapter = new SearchUserRecyclerViewAdapter(usersList);
        mManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mManager);
        recyclerView.setAdapter(mAdapter);
    }
    private void initSearch(MenuItem searchMenuItem) {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchMenuItem.expandActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });
    }

    private void search(String user_name) {
        usersList.clear();
        if(user_name.length() > 0) {
            showLocalProgress();
        }
        else
        {
            hideLocalProgress();
        }
        Query que = fStore.collection("users").whereEqualTo("username", user_name);
        que.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (!documentSnapshots.isEmpty()) {
                            for (DocumentSnapshot docSnap : documentSnapshots.getDocuments()) {
                                User user = docSnap.toObject(User.class);
                                user.setUser_id(docSnap.getId());
                                usersList.add(user);
                                hideEmptyListLayout();
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getLocalizedMessage());
                        hideLocalProgress();
                        showSnackBar("Please Try Again Later");
                    }
                });
    }
    public void hideEmptyListLayout() {
        hideLocalProgress();
        emptyListMessageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
    public void showEmptyListLayout() {
        hideLocalProgress();
        recyclerView.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        initSearch(searchMenuItem);

        return true;
    }
    public void showLocalProgress() {
        AnimationUtils.showViewByScaleWithoutDelay(progressBar);
    }

    public void hideLocalProgress() {
        AnimationUtils.hideViewByScale(progressBar);
    }

}
