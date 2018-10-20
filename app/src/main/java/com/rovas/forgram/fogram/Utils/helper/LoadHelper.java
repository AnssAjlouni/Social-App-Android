package com.rovas.forgram.fogram.Utils.helper;

import android.support.annotation.NonNull;

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
import com.google.firebase.messaging.FirebaseMessaging;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.Notification;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class LoadHelper {
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore fStore;
    private static List<Notification> notificationList;
    private static String current_user_id;//Current_user_id>> Using Firebase Auth
    public static void Load() {
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        current_user_id = mAuth.getCurrentUser().getUid();//get current_user_id
        FirebaseMessaging.getInstance().subscribeToTopic("promotional_messages");
        fStore.collection("users").document(current_user_id).get() // Check The Database fof the document
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                UserWiazrd.getInstance().getTempUser().setUsername(task.getResult().getString("username"));
                                UserWiazrd.getInstance().getTempUser().setName(task.getResult().getString("name"));
                                UserWiazrd.getInstance().getTempUser().setStatus(task.getResult().getString("status"));
                                UserWiazrd.getInstance().getTempUser().setThumb_image(task.getResult().getString("thumb_image"));
                                UserWiazrd.getInstance().getTempUser().setToken_id(task.getResult().getString("token_id"));
                                UserWiazrd.getInstance().getTempUser().setRole(task.getResult().getLong("role"));
                                fStore.collection("users").document(current_user_id).collection("Followers")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    UserWiazrd.getInstance().getTempUser().setFollowers(task.getResult().size());
                                                }
                                            }
                                        });
                                fStore.collection("users").document(current_user_id).collection("Following")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    UserWiazrd.getInstance().getTempUser().setFollowing(task.getResult().size());
                                                }
                                            }
                                        });
                                fStore.collection("user_Posts").document(current_user_id).collection(current_user_id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    UserWiazrd.getInstance().getTempUser().setPosts(task.getResult().size());
                                                }
                                            }
                                        });
                                /*
                                Query f_query = fStore.collection("users/" + current_user_id + "/Notifications")
                                        .orderBy("timestamp", Query.Direction.DESCENDING);
                                f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        if (!documentSnapshots.isEmpty()) {
                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                                    String notificationid = doc.getDocument().getId();
                                                    final Notification notification = doc.getDocument().toObject(Notification.class).withid(notificationid);
                                                    notificationList.add(notification);
                                                }
                                            }
                                        }
                                    }
                                });*/
                                /*
                                if(UserWiazrd.getInstance().getTempUser().getNotificationList() == null)
                                {
                                    UserWiazrd.getInstance().getTempUser().setNotificationList(notificationList);
                                }
                                else {
                                    UserWiazrd.getInstance().getTempUser().setNotificationList(null);
                                    UserWiazrd.getInstance().getTempUser().setNotificationList(notificationList);
                                }
                                */
                                //Start

                            }
                        }
                        //else = if unsuccessful
                    }
                    //on failure
                });
    }
}
