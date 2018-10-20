package com.rovas.forgram.fogram.controllers.fragments_home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rovas.forgram.fogram.T.PushNotification;
import com.rovas.forgram.fogram.base.BaseFragment;
import com.rovas.forgram.fogram.Utils.UI.PostUI;
import com.rovas.forgram.fogram.controllers.activities_popup.ImagePopupDetailsActivity;
import com.rovas.forgram.fogram.controllers.activities_profile.EditProfileActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.BlogPost;
import com.rovas.forgram.fogram.views.RecyclerViewAdapter;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.views.BlogRecyclyerAdapter;
import com.rovas.forgram.fogram.controllers.activities_profile.FollowersActivity;
import com.rovas.forgram.fogram.controllers.activities_profile.FollowingActivity;
import com.rovas.forgram.fogram.models.StoriesPost;
import com.rovas.forgram.fogram.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class ProfileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener , BlogRecyclyerAdapter.PublishClickListener {
    private static final String TAG = "ProfileFragment";
    //models
    private List<StoriesPost> stories_list;
    private List<BlogPost> blog_list;
    private List<User> user_list;
    //RecycleAdapters
    private RecyclerViewAdapter sRecyclerViewAdapter;
    private BlogRecyclyerAdapter blogRecyclyerAdapter;
    //RecycleViews
    private RecyclerView blog_list_view;
    private RecyclerView recyclerView;
    //
    private FragmentActivity myContext;
    private ImageView mProfile;
    private TextView mName;
    private TextView mNickName;
    private Button mEdit;
    //
    private StorageReference sReference;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String user_id;
    private Uri mainImageUri = null;
    private Bitmap compressedImageFile;
    //
    private LinearLayoutManager mManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    //
    private LinearLayout lFollowing;
    private LinearLayout lFollowers;
    private TextView mPosts, mFollowers, mFollowing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile , container , false);

        //
        fStore = FirebaseFirestore.getInstance();
        mAuth =FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        sReference = FirebaseStorage.getInstance().getReference();
        //
        mProfile = (ImageView) view.findViewById(R.id.profile_img);
        mName = (TextView) view.findViewById(R.id.profile_name);
        mNickName = (TextView) view.findViewById(R.id.profile_nickName);
        mEdit = (Button) view.findViewById(R.id.edit_btn);
        //
        mPosts = (TextView) view.findViewById(R.id.posts_count);
        mFollowers = (TextView) view.findViewById(R.id.followers_count);
        mFollowing = (TextView) view.findViewById(R.id.following_count);
        //Progress Dialog

        getFollowersCount();
        getFollowingCount();
        getPostsCount();
        //
        lFollowers = (LinearLayout)view.findViewById(R.id.followers_con);
        lFollowing = (LinearLayout)view.findViewById(R.id.following_con);
        lFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FIntent = new Intent(getContext(), FollowersActivity.class);
                FIntent.putExtra("user_id", user_id);
                startActivity(FIntent);
            }
        });
        lFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent FIntent = new Intent(getContext(), FollowingActivity.class);
                FIntent.putExtra("user_id", user_id);
                startActivity(FIntent);
            }
        });
        //
        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent loginIntent = new Intent(getContext(), UploadingActivity.class);
                //startActivity(loginIntent);
               // Intent intent1 = new Intent(getContext(), CameraActivity.class);
               // startActivity(intent1);
                //Intent intent1 = new Intent(getContext(), TMessageActivity.class);
                //startActivity(intent1);
                //Intent intent1 = new Intent(getContext(), DownloadActivity.class);
                //startActivity(intent1);

                //Intent intent1 = new Intent(getContext(), DownloadFileManager.class);
                //startActivity(intent1);
                //Intent intent1 = new Intent(getContext(), ConversationListActivity.class);
                //startActivity(intent1);
                //Intent intent1 = new Intent(getContext(), PushNotification.class);
                //startActivity(intent1);

                Intent intent1 = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent1);
                /*
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
                */
            }


        });
        mName.setText(UserWiazrd.getInstance().getTempUser().getName());
        mNickName.setText("@" + UserWiazrd.getInstance().getTempUser().getUsername());
        if(UserWiazrd.getInstance().getTempUser().getThumb_image() != null) {
            mainImageUri = Uri.parse(UserWiazrd.getInstance().getTempUser().getThumb_image());
            Glide.with(getContext())
                    .load(mainImageUri)
                    .into(mProfile);
        }
        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]{"View Picture", "New Proile BlogPost"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        String username = UserWiazrd.getInstance().getTempUser().getUsername();
                        if(i == 0){
                            if(mainImageUri != null) {
                                Intent chatIntent = new Intent(getContext(), ImagePopupDetailsActivity.class);
                                chatIntent.putExtra(PostUI.PROFILE_IMAGE_BUNDLE, mainImageUri.toString());
                                chatIntent.putExtra(PostUI.PROFILE_USERNAME_BUNDLE, username);
                                startActivity(chatIntent);
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Empty Profile Image", Toast.LENGTH_SHORT).show();
                            }

                        }

                        if(i == 1) {
                        }
                    }
                });

                builder.show();
            }
        });
        //
        //Recent_posts_Lists
        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();//
        //
        blog_list_view = view.findViewById(R.id.ViewPager_profile);
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
        getData();
        return view;

    }
    @Override
    public void onRefresh() {
        getData();

    }
    public void getData(){
        try {
            swipeRefreshLayout.setRefreshing(true);
            getAllPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getAllPosts()
    {
        blog_list.clear();
        if(mAuth.getCurrentUser() != null) {

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                    super.onScrolled(recyclerView, dx, dy);


                }
            });
            //mUserDatabase.keepSynced(true);
            //CollectionReference collectionReference = fStore.collection("Posts");
           // collectionReference.whereEqualTo("user_id" , user_id);
            Query f_query =  fStore.collection("Posts").whereEqualTo("user_id" , user_id);
            f_query.addSnapshotListener(getActivity() ,new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            final String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withid(blogPostId);
                            blog_list.add(blogPost);

                            String thumb_image = UserWiazrd.getInstance().getTempUser().getThumb_image();
                            HashMap<String, Object> userMap_ = new HashMap<>();
                            userMap_.put("thumb_image", thumb_image);
                            fStore.collection("Posts").document(blogPostId).update(userMap_);
                            blogRecyclyerAdapter.notifyDataSetChanged();

                        }
                    }


                }
            });


        }
        swipeRefreshLayout.setRefreshing(false);

    }
    private void getFollowersCount(){
        mFollowers.setText(String.valueOf(UserWiazrd.getInstance().getTempUser().getFollowers()));
    }

    private void getFollowingCount(){
        mFollowing.setText(String.valueOf(UserWiazrd.getInstance().getTempUser().getFollowing()));
    }

    private void getPostsCount(){
        mPosts.setText(String.valueOf(UserWiazrd.getInstance().getTempUser().getPosts()));
    }

    @Override
    public void onPublishClicked() {
        //startActivity(new Intent(getContext(), ShareActivity.class));
    }

}
