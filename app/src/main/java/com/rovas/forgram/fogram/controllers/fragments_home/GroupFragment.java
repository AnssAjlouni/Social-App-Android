package com.rovas.forgram.fogram.controllers.fragments_home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.views.ConversationRecyclerViewAdapter;
import com.rovas.forgram.fogram.models.ChatGroup;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class GroupFragment extends Fragment {

    private List<ChatGroup> chatList;
    RecyclerView recyclerView;
    ConversationRecyclerViewAdapter chatRecyclerViewAdapter;

    private FirebaseAuth mAuth;
    String User_id;
    private FirebaseFirestore fStore;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat , container , false);
        mAuth = FirebaseAuth.getInstance();
        User_id = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        //user_notification = databaseReference.child(User_id).child("Notifications");

        recyclerView = (RecyclerView)view.findViewById(R.id.conv_list);
        chatList = new ArrayList<>();

        chatRecyclerViewAdapter = new ConversationRecyclerViewAdapter(chatList,getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(chatRecyclerViewAdapter);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        All_Chats();

    }

    private void All_Chats() {
        chatList.clear();
        fStore.collection("groups")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String chatiD = doc.getDocument().getId();
                                    ChatGroup chat = doc.getDocument().toObject(ChatGroup.class).withid(chatiD);
                                    chatList.add(chat);
                                    chatRecyclerViewAdapter.notifyDataSetChanged();



                                }
                            }

                        }
                    }
                });
    }

}
