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
import com.rovas.forgram.fogram.interfaces.OnGroupClickListener;
import com.rovas.forgram.fogram.controllers.activities_chatgroup.GroupChatSQLActivity;
import com.rovas.forgram.fogram.models.ChatGroup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Mohamed El Sayed
 */
public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder>{
    private List<ChatGroup> chatlist;
    private Context context;
    public FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    private OnGroupClickListener onGroupClickListener;

    public OnGroupClickListener getOnGroupClickListener() {
        return onGroupClickListener;
    }

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        this.onGroupClickListener = onGroupClickListener;
    }

    public ConversationRecyclerViewAdapter(List<ChatGroup> chatlist, Context context) {
        this.chatlist = chatlist;
        this.context = context;
    }


    @NonNull
    @Override
    public ConversationRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_item,parent,false);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ConversationRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationRecyclerViewAdapter.ViewHolder holder, final int position) {

        String current_user_id = mAuth.getCurrentUser().getUid();
        final String from_user_id = chatlist.get(position).getGroupId();
        /*
        //TODO
        String message = chatlist.get(position).getMessage();
        holder.setMessage(message , false);
        */
        final String user_name = chatlist.get(position).getName();
        holder.setName(user_name);
        final String thumb_image = chatlist.get(position).getIconURL();
        holder.setUserImage(thumb_image);
        long time = chatlist.get(position).getCreatedOnLong();
        holder.setTime(time);
        holder.select_chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context, GroupChatSQLActivity.class);
                chatIntent.putExtra("user_id", from_user_id);
                chatIntent.putExtra("user_name", user_name);
                context.startActivity(chatIntent);
            }
        });
        ChatGroup chatGroup = chatlist.get(position);
        setMembers(holder, chatGroup);

        setOnGroupClickListener(holder, chatGroup, position, getOnGroupClickListener());




    }

    private void setMembers(ConversationRecyclerViewAdapter.ViewHolder holder, ChatGroup chatGroup) {

        String members;
        if (chatGroup.getMembersList() != null && chatGroup.getMembersList().size() > 0) {
            members = chatGroup.printMembersListWithSeparator(", ");
        } else {
            // if there are no members show the logged user as "you"
            members = holder.itemView.getContext().getString(R.string.activity_message_list_group_info_you_label);
        }

        holder.user_last_message.setText(members);
    }

    private void setOnGroupClickListener(
            ConversationRecyclerViewAdapter.ViewHolder holder,
            final ChatGroup chatGroup,
            final int position,
            final OnGroupClickListener callback) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onGroupClicked(chatGroup, position);
            }
        });
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

            Glide.with(context).load(thumb_image).into(user_single_image);

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
