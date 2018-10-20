package com.rovas.forgram.fogram.controllers.fragments_home;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.Message_Status;
import com.rovas.forgram.fogram.Utils.UI.ChatUI;
import com.rovas.forgram.fogram.controllers.activities_chatgroup.AddMemberToChatGroupActivity;
import com.rovas.forgram.fogram.controllers.activities_main.SearchActivity;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.managers.chat_DB.DB_SqLite_Chat;
import com.rovas.forgram.fogram.managers.chat_DB.DB_Utils;
import com.rovas.forgram.fogram.models.Conversation;
import com.rovas.forgram.fogram.views.ChatRecyclerViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class ChatFragment  extends Fragment {

    private List<Conversation> chatList;
    RecyclerView recyclerView;
    ChatRecyclerViewAdapter chatRecyclerViewAdapter;

    private FirebaseAuth mAuth;
    String User_id;
    private FirebaseFirestore fStore;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static DB_SqLite_Chat db_chat;
    private String dbName;
    private File yourFile;
    private DB_Utils DBUtils;
    private LinearLayout new_group;
    FloatingActionButton F_menu;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat , container , false);
        mAuth = FirebaseAuth.getInstance();
        User_id = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        F_menu = (FloatingActionButton) view.findViewById(R.id.fab_new_post);
        if(UserWiazrd.getInstance().getTempUser().getRole() == 1 || UserWiazrd.getInstance().getTempUser().getRole() == 2) {
            new_group = (LinearLayout) view.findViewById(R.id.box_add_group);
            new_group.setVisibility(View.VISIBLE);
            new_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user_name = UserWiazrd.getInstance().getTempUser().getUsername();
                    Intent intent = new Intent(getContext(),
                            AddMemberToChatGroupActivity.class);
                    intent.putExtra(ChatUI.BUNDLE_CURRENT_USERNAME, user_name);
                    startActivity(intent);
                }
            });
        }
        F_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });
        //user_notification = databaseReference.child(User_id).child("Notifications");

        recyclerView = (RecyclerView)view.findViewById(R.id.conv_list);
        chatList = new ArrayList<>();

        chatRecyclerViewAdapter = new ChatRecyclerViewAdapter(chatList,getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        recyclerView.setAdapter(chatRecyclerViewAdapter);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Database_Conv();
        All_Chats_();

    }

    private void Database_Conv() {
        dbName = "msgstore.db";
        DBUtils = new DB_Utils(getContext());
        yourFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "FutureAcademy"
                + File.separator + "Databases"
                + File.separator + dbName);
        db_chat = new DB_SqLite_Chat(getContext(), dbName, null, 1);
        db_chat.queryData(Conversation.CREATE_TABLE_EXIST("conversations"));
        //Log.d(TAG, "getChatFromDatabaseMomery:   db_conv.queryData(Message.CREATE_TABLE_EXIST(\"conversations\"));");

        //getChatFromDatabaseMomery(dbName);
    }

    public void getChatFromDatabaseMomery(String dbName )
    {   File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "FutureAcademy"
            + File.separator + "Databases"
            + File.separator + dbName);
        if(file.exists()) {
            if(db_chat.getAllrecordFromMomeryConv(getActivity() ,"conversations",yourFile).size() > 0)
            {
                chatList.clear();
                chatList.addAll(db_chat.getAllrecordFromMomeryConv(getActivity(),"conversations" ,yourFile));
                //Log.d(TAG, "getChatFromDatabaseMomery:  chatList.addAll(db_conv.getAllrecordFromMomery(getActivity(),\"conversations\" ,yourFile)");
                chatRecyclerViewAdapter.notifyDataSetChanged();
            }
            else
            {
                getChatFromDatabase();
            }
        }
        else
        {
            getChatFromDatabase();
        }


    }
    public void getChatFromDatabase()
    {

        chatList.clear();
        //Log.d(TAG, "getChatFromDatabaseMomery: " + db_chat.getAllrecordConv("conversations").size());
        chatList.addAll(db_chat.getAllrecordConv("conversations"));
        chatRecyclerViewAdapter.notifyDataSetChanged();
        //Log.d(TAG, "getChatFromDatabaseMomery: " + chatList.size());


    }
    private void All_Chats_() {
        chatList.clear();
        Query f_query =  fStore.collection("chat/" + User_id + "/with")
                .orderBy("time_stamp", Query.Direction.DESCENDING);
        f_query.addSnapshotListener(getActivity() , new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String chatiD = doc.getDocument().getId();
                        Conversation chat = doc.getDocument().toObject(Conversation.class).withid(chatiD);
                        chatList.add(chat);
                        chatRecyclerViewAdapter.notifyDataSetChanged();
                        //
                        String type = chat.getChannelType();
                        String from_id = chat.getFrom_id();

                            if (type.equals(Message_Status.DIRECT_CHANNEL_TYPE)) {
                                if(!from_id.equals(User_id)) {
                                    Query f_query = fStore.collection("messages").document(User_id).collection(from_id)
                                            .orderBy("time_stamp", Query.Direction.DESCENDING).limit(1);
                                    f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                                String message = doc.getDocument().getString("message");
                                                long time = doc.getDocument().getLong("time_stamp");
                                                String type = doc.getDocument().getString("type");
                                                HashMap<String, Object> userMap_ = new HashMap<>();
                                                userMap_.put("message", message);
                                                userMap_.put("type", type);
                                                userMap_.put("time_stamp", time);
                                                fStore.collection("chat").document(User_id).collection("with").document(from_id).update(userMap_);
                                            }
                                        }

                                    });
                                }
                            } else if (type.equals(Message_Status.GROUP_CHANNEL_TYPE)) {
                                Query f_query = fStore.collection("groups").document(chatiD).collection("messages")
                                        .orderBy("time_stamp", Query.Direction.DESCENDING).limit(1);
                                f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                            String message = doc.getDocument().getString("message");
                                            long time = doc.getDocument().getLong("time_stamp");
                                            String type = doc.getDocument().getString("type");
                                            HashMap<String, Object> userMap_ = new HashMap<>();
                                            userMap_.put("message", message);
                                            userMap_.put("type", type);
                                            userMap_.put("time_stamp", time);
                                            fStore.collection("chat").document(User_id).collection("with").document(from_id).update(userMap_);
                                        }
                                    }
                                });
                            }

                        //
                       // String from_name = chat.getFrom_name();
                       // String from_id = chat.getFrom_id();
                       // String message = chat.getMessage();
                       // String thumb_image =chat.getThumb_image();
                       // String type = chat.getChannelType();
                       // long timestamp = chat.getTime_stamp();
                       // long insert_id = db_chat.insertDataConv("conversations",from_name, from_id, message , thumb_image, type,timestamp  , false , false);
                    }
                }
            }
        });
    }
    private void All_Chats() {
        chatList.clear();
        Query f_query =  fStore.collection("chat/" + User_id + "/with")
                .whereEqualTo("seen" , false);//limit error [limittofirst]
        f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String chatiD = doc.getDocument().getId();
                        Conversation chat = doc.getDocument().toObject(Conversation.class).withid(chatiD);
                        //chatList.add(chat);
                        //chatRecyclerViewAdapter.notifyDataSetChanged();

                        String from_name = chat.getFrom_name();
                        String from_id = chat.getFrom_id();
                        String message = chat.getMessage();
                        String thumb_image =chat.getThumb_image();
                        String type = chat.getChannelType();
                        long timestamp = chat.getTime_stamp();
                        long insert_id = db_chat.insertDataConv("conversations",from_name, from_id, message , thumb_image, type,timestamp  , false , false);
                        Show_Last_Message(insert_id);
                        HashMap<String, Object> MessageMap_ = new HashMap<>();
                        MessageMap_.put("seen" , true);
                        fStore.collection("chat").document(User_id).collection("with").document(chatiD).update(MessageMap_);
                        //playBeep();
                    }
                }
            }
        });
    }
    private void Show_Last_Message(long insert_id) {
        chatList.add(db_chat.getConversation("conversations",insert_id)); //Fake Postition
        DBUtils.copyDatabase(dbName);
        chatRecyclerViewAdapter.notifyDataSetChanged();
    }
    public void playBeep() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
