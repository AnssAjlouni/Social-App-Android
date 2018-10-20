package com.rovas.forgram.fogram.controllers.fragments_home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.controllers.activities_editor.ImagePostEditorActivity;
import com.rovas.forgram.fogram.controllers.activities_editor.StoriesEditorActivity;
import com.rovas.forgram.fogram.controllers.activities_main.SignInActivity;
import com.rovas.forgram.fogram.controllers.activities_publish.PhotosActivity;
import com.rovas.forgram.fogram.controllers.activities_publish.TweetsActivity;
import com.rovas.forgram.fogram.interfaces.EndlessRecyclerViewScrollListener;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.models.StoriesPost;

import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.views.RecyclerViewAdapter;

import com.rovas.forgram.fogram.views.BlogRecyclyerAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Mohamed El Sayed
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener ,  RecyclerViewAdapter.HeaderClickListener , BlogRecyclyerAdapter.PublishClickListener {
    private static final String TAG = "HomeFragment";
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    DocumentSnapshot lastVisible;
    private boolean isLastItemReached = false;
    private Boolean isScrolling = false;

    //Fragment
    private FragmentActivity myContext;
    //models
    private List<StoriesPost> stories_list;
    private List<BlogPost> blog_list;
    //RecycleAdapters
    private RecyclerViewAdapter sRecyclerViewAdapter;
    private BlogRecyclyerAdapter blogRecyclyerAdapter;
    //RecycleViews
    private RecyclerView blog_list_view;
    private RecyclerView recyclerView;
    //FireBase
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    //
    private LinearLayoutManager mManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    //
    FloatingActionButton F_menu , F_tweet , F_photo ;
    Animation FabOpen , FabClose , FabClockWise , FabAntiClockWise;
    Boolean isOpen = false;
    //

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home , container , false);
        //FireBase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        //Views
        //Recent_posts_Lists
        F_menu = (FloatingActionButton) view.findViewById(R.id.fab_new_post);
        F_photo = (FloatingActionButton) view.findViewById(R.id.fab_new_post_photo);
        F_tweet = (FloatingActionButton) view.findViewById(R.id.fab_new_post_tweet);
        FabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        FabClockWise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        FabAntiClockWise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlockwise);
        initViews();
        blog_list = new ArrayList<>();
        //
        blog_list_view = view.findViewById(R.id.blog_list_view);
        blog_list_view.setHasFixedSize(true);
        //
        blogRecyclyerAdapter = new BlogRecyclyerAdapter(blog_list , this);
        //blog_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        //

        mManager = new LinearLayoutManager(getActivity());
        //mManager.setReverseLayout(true);
        //mManager.setStackFromEnd(true);
        blog_list_view.setLayoutManager(mManager);
        //
        //
        blog_list_view.setAdapter(blogRecyclyerAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        //
        stories_list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView_stories);
        sRecyclerViewAdapter = new RecyclerViewAdapter(stories_list , this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(sRecyclerViewAdapter);
        getData();


        //

        return view;

    }

    private void initViews() {

        F_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    F_tweet.startAnimation(FabClose);
                    F_photo.startAnimation(FabClose);
                    F_menu.startAnimation(FabAntiClockWise);
                    F_tweet.setClickable(false);
                    F_photo.setClickable(false);
                    isOpen = false;
                } else {
                    F_tweet.startAnimation(FabOpen);
                    F_photo.startAnimation(FabOpen);
                    F_menu.startAnimation(FabClockWise);
                    F_tweet.setClickable(true);
                    F_photo.setClickable(true);
                    isOpen = true;
                }
            }
        });
        F_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ImagePostEditorActivity.class));
                //startActivity(new Intent(HomeActivity.this, AddMemberToChatGroupActivity.class));
                //startService(new Intent(HomeActivity.this ,MessagingService.class));
                //Intent home_intent = new Intent(myContext, NewPostActivity.class);//ACTIVITY_NUM = 0
                //myContext.startActivity(home_intent);//content because the Class
            }
        });
        F_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TweetsActivity.class));
                //Intent home_intent = new Intent(myContext, NewPostActivity.class);//ACTIVITY_NUM = 0
                //myContext.startActivity(home_intent);//content because the Class
            }
        });
    }

    public void getAllPosts()
    {
        blog_list.clear();
        stories_list.clear();
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

            Query f_query = fStore.collection("Posts" ).orderBy("time_stamp" ,Query.Direction.DESCENDING).limit(10) ;
            f_query.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
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
                                            fStore.collection("Posts").document(blogPostId).update(userMap_);
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


        //
        if(mAuth.getCurrentUser() != null) {
            fStore = FirebaseFirestore.getInstance();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);



                }
            });
            //mUserDatabase.keepSynced(true);
            Query f_query = fStore.collection("Stories").orderBy("time_stamp" ,Query.Direction.DESCENDING);
            //.orderBy("time_stamp" ,Query.Direction.DESCENDING);
            f_query.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String sPostId = doc.getDocument().getId();
                            StoriesPost sPost = doc.getDocument().toObject(StoriesPost.class).withid(sPostId);
                            stories_list.add(sPost);
                            //String sPostId_ = doc.getDocument().getString("user_id");
                            fStore.collection("users").document(sPostId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                            fStore.collection("Stories").document(sPostId).update(userMap_);
                                        }


                                    }
                                }
                            });
                            sRecyclerViewAdapter.notifyDataSetChanged();
                            //Toast.makeText(getContext(), "Passed", Toast.LENGTH_SHORT).show();

                        }
                    }

                }
            });



        }
        swipeRefreshLayout.setRefreshing(false);

    }

    public void getData(){
            try {
                swipeRefreshLayout.setRefreshing(true);
                getAllPosts();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Map<String , Object > tokenMap = new HashMap<>();
            tokenMap.put("token_id" , FieldValue.delete());
            tokenMap.put("online" , timestamp.getTime());
            fStore.collection("users").document(user_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext() , SignInActivity.class));
                }
            });


            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onRefresh() {
        getData();


    }
    @Override
    public void onHeaderClicked() {
        //selectImage();
        Intent intent1 = new Intent(getContext(), StoriesEditorActivity.class);
        startActivity(intent1);
    }

    @Override
    public void onPublishClicked() {
        //startActivity(new Intent(myContext, ShareActivity.class));
    }
    /////////////////////////////
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int mPageCount , int ItemCount) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        //mUserDatabase.keepSynced(true);
       // blog_list.clear();
        Query f_query = fStore.collection("Posts" ).orderBy("time_stamp" ,Query.Direction.DESCENDING)
                //.startAfter()
                .limit((mPageCount) * ItemCount);
        f_query.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                lastVisible = documentSnapshots.getDocuments()
                        .get(documentSnapshots.size() - 1);
                if (!isLastItemReached) {
                    //mUserDatabase.keepSynced(true);
                    Query f_query = fStore.collection("Posts").orderBy("time_stamp", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit((mPageCount) * ItemCount);
                    f_query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    final String blogPostId = doc.getDocument().getId();
                                    BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withid(blogPostId);
                                    blog_list.add(blogPost);

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
                                                    fStore.collection("Posts").document(blogPostId).update(userMap_);
                                                    Log.d(TAG, "HomePage:  "+mPageCount+" Page Loaded");
                                                }


                                            }
                                            if (documentSnapshots.size() < mPageCount * ItemCount) {

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
        });
    }
    public void loadNextDataFromApi() {
        Query f_query = fStore.collection("Posts").orderBy("time_stamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10);
        f_query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
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
                                        fStore.collection("Posts").document(blogPostId).update(userMap_);
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
    /////////////////////////////


}
