package com.rovas.forgram.fogram.controllers.fragments_home;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.Notification;
import com.rovas.forgram.fogram.views.NotificationRecyclerViewAdapter;
import com.rovas.forgram.fogram.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed El Sayed
 */
public class NotificationFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private List<Notification> notificationList;
    RecyclerView recyclerView;
    NotificationRecyclerViewAdapter notificationRecyclerViewAdapter;

    private FirebaseAuth mAuth;
    String User_id;
    private FirebaseFirestore fStore;
    private SwipeRefreshLayout swipeRefreshLayout;
    //
    //
    public NotificationFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        mAuth = FirebaseAuth.getInstance();
        User_id = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        //user_notification = databaseReference.child(User_id).child("Notifications");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycleView_notification);
        notificationList = new ArrayList<>();

        notificationRecyclerViewAdapter = new NotificationRecyclerViewAdapter(notificationList,getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        recyclerView.setAdapter(notificationRecyclerViewAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Simple_Load();

    }

    @Override
    public void onRefresh() {
        getData();
    }
    public void getData(){
        try {
            swipeRefreshLayout.setRefreshing(true);
            //All_Notification();
            Simple_Load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Simple_Load() {
        notificationList.clear();
        //notificationList.addAll(UserWiazrd.getInstance().getTempUser().getNotificationList());
        Query f_query = fStore.collection("users/" + User_id + "/Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(25);
        f_query.addSnapshotListener(getActivity() , new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String notificationid = doc.getDocument().getId();
                            Notification notification = doc.getDocument().toObject(Notification.class).withid(notificationid);
                            notificationList.add(notification);
                            notificationRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    String post_id;
    String username;
    private void All_Notification() {
        notificationList.clear();
        Query f_query =  fStore.collection("users/" + User_id + "/Notifications").orderBy("timestamp" ,Query.Direction.DESCENDING);;
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String notificationid = doc.getDocument().getId();
                            final Notification notification = doc.getDocument().toObject(Notification.class).withid(notificationid);
                            if (notificationid.contains("%")) {
                                String[] parts = notificationid.split("%");
                                post_id = parts[0];
                                username = parts[1];
                                fStore.collection("Posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                notificationList.add(notification);

                                            } else {
                                                fStore.collection("users/" + User_id + "/Notifications/").document(post_id + "%" + username).delete();
                                            }
                                        }
                                    }
                                });
                                //Toast.makeText(getContext(), "E :" + post_id, Toast.LENGTH_SHORT).show();
                            } else if (notificationid.contains("!")) {
                                String[] parts = notificationid.split("!");
                                post_id = parts[0];
                                username = parts[1];
                                fStore.collection("Posts").document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                notificationList.add(notification);

                                            } else {
                                                fStore.collection("users/" + User_id + "/Notifications/").document(post_id + "!" + username).delete();
                                            }
                                        }
                                    }
                                });
                                notificationRecyclerViewAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }
}
