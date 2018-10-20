package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.GetTimeAgo;
import com.rovas.forgram.fogram.Utils.Message_Status;
import com.rovas.forgram.fogram.controllers.activities_chat.ChatSQLActivity;
import com.rovas.forgram.fogram.controllers.activities_chatgroup.GroupChatSQLActivity;
import com.rovas.forgram.fogram.managers.chat_DB.DB_SqLite_Chat;
import com.rovas.forgram.fogram.models.Conversation;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohamed El Sayed
 */
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder>{
    private List<Conversation> chatlist;
    private Context context;
    public FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    public static DB_SqLite_Chat db_chat;
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    public ChatRecyclerViewAdapter(List<Conversation> chatlist, Context context) {
        this.chatlist = chatlist;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_item,parent,false);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db_chat = new DB_SqLite_Chat(context, "msgstore.db", null, 1);
        db_chat.queryData(Conversation.CREATE_TABLE_EXIST("conversations"));
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ChatRecyclerViewAdapter.ViewHolder holder, final int position) {

        String current_user_id = mAuth.getCurrentUser().getUid();
        final String from_user_id = chatlist.get(position).getFrom_id();
        final String user_name = chatlist.get(position).getFrom_name();
        holder.setName(user_name);
        String message = chatlist.get(position).getMessage();
        holder.setMessage(message , false);
        final String thumb_image = chatlist.get(position).getThumb_image();
        holder.setUserImage(thumb_image);
        long time = chatlist.get(position).getTime_stamp();
        holder.setTime(time);
        //getLastrecord
        String type = chatlist.get(position).getChannelType();
        if(type.equals(Message_Status.DIRECT_CHANNEL_TYPE)) {
            holder.select_chat_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatIntent = new Intent(context, ChatSQLActivity.class);
                    chatIntent.putExtra("user_id", from_user_id);
                    chatIntent.putExtra("user_name", user_name);
                    context.startActivity(chatIntent);
                }
            });
        }
        else if(type.equals(Message_Status.GROUP_CHANNEL_TYPE))
        {
            holder.select_chat_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chatIntent = new Intent(context, GroupChatSQLActivity.class);
                    chatIntent.putExtra("user_id", from_user_id);
                    chatIntent.putExtra("user_name", user_name);
                    context.startActivity(chatIntent);
                    /*
                    fStore.collection("groups").document(from_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ChatGroup chatGroup = document.toObject(ChatGroup.class);
                                Intent chatIntent = new Intent(context, GroupChatSQLActivity.class);
                                chatIntent.putExtra("user_id", from_user_id);
                                chatIntent.putExtra("user_name", user_name);
                                chatIntent.putExtra(ChatUI.BUNDLE_GROUP_ID, chatGroup);
                                context.startActivity(chatIntent);
                                Log.d(TAG, "onComplete: ChatGroup Loaded");
                            }
                        }
                    });
                    */
                    /*
                    fStore.collection("groups").whereEqualTo("groupId" , from_user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    // here you can get the id.
                                    ChatGroup chatGroup = documentSnapshot.toObject(ChatGroup.class).withid(documentSnapshot.getId());
                                    Intent chatIntent = new Intent(context, GroupChatSQLActivity.class);
                                    chatIntent.putExtra("user_id", from_user_id);
                                    chatIntent.putExtra("user_name", user_name);
                                    chatIntent.putExtra(ChatUI.BUNDLE_GROUP_ID, chatGroup);
                                    context.startActivity(chatIntent);
                                    Log.d(TAG, "onComplete: ChatGroup Loaded");
                                    // you can apply your actions...
                                }
                            }
                            else
                            {
                                Intent chatIntent = new Intent(context, GroupChatSQLActivity.class);
                                chatIntent.putExtra("user_id", from_user_id);
                                chatIntent.putExtra("user_name", user_name);
                                context.startActivity(chatIntent);
                            }
                        }
                    });
                    */
                    /*
                    Query f_query = fStore.collection("groups").whereEqualTo("groupId" , from_user_id);
                    f_query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String groupID = doc.getDocument().getId();
                                    ChatGroup sGroup = doc.getDocument().toObject(ChatGroup.class).withid(groupID);
                                    Log.d(TAG, "onComplete: ChatGroup Loaded");
                                    Intent chatIntent = new Intent(context, GroupChatSQLActivity.class);
                                    chatIntent.putExtra("user_id", from_user_id);
                                    chatIntent.putExtra("user_name", user_name);
                                    chatIntent.putExtra(ChatUI.BUNDLE_GROUP_ID, sGroup);
                                    context.startActivity(chatIntent);

                                }
                            }
                        }
                    });
                    */
                }
            });
        }





    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private ImageView online;
        private TextView user_single_name;
        private TextView user_last_message;
        private CircleImageView user_single_image;
        private TextView user_single_time;
        private RelativeLayout select_chat_layout;
        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            user_single_image = (CircleImageView) view.findViewById(R.id.user_single_image);
            online = (ImageView) view.findViewById(R.id.user_single_online_icon);
            user_single_name = (TextView)view.findViewById(R.id.user_single_name);
            user_last_message = (TextView)view.findViewById(R.id.user_single_message);
            user_single_time = (TextView)view.findViewById(R.id.user_single_time);
            select_chat_layout = (RelativeLayout) view.findViewById(R.id.select_chat_layout);

        }
        public void setMessage(String message, boolean isSeen){


            user_last_message.setText(message);

            if(!isSeen){
                user_last_message.setTypeface(user_last_message.getTypeface(), Typeface.BOLD);
            } else {
                user_last_message.setTypeface(user_last_message.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name){

            user_single_name.setText(name);

        }

        public void setUserImage(String thumb_image){

            if(thumb_image != null) {
                Glide.with(context).load(thumb_image).into(user_single_image);
            }

        }
        public void setTime(long time)
        {
            GetTimeAgo getTimeAgo = new GetTimeAgo();

            long lastTime = time;

            String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, context);

            user_single_time.setText(lastSeenTime);
        }



    }
}