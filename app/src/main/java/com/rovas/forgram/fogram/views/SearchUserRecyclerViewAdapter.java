package com.rovas.forgram.fogram.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Synchronizer;
import com.rovas.forgram.fogram.controllers.activities_popup.vProfileActivity;
import com.rovas.forgram.fogram.interfaces.OnContactClickListener;
import com.rovas.forgram.fogram.models.User;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Mohamed El Sayed
 */
public class SearchUserRecyclerViewAdapter extends RecyclerView.Adapter<SearchUserRecyclerViewAdapter.ViewHolder>
        implements Filterable  {
    private List<User> user_list;
    private List<User> user_list_Filtered;

    private Context context;
    public FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    private OnContactClickListener onContactClickListener;
    public void setOnContactClickListener(OnContactClickListener onContactClickListener) {
        this.onContactClickListener = onContactClickListener;
    }

    public OnContactClickListener getOnContactClickListener() {
        return onContactClickListener;
    }
    public void setList(List<User> list) {
        this.user_list = list;
        this.user_list_Filtered =list;
    }
    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    public SearchUserRecyclerViewAdapter(List<User> user_list) {
        this.user_list = user_list;
        this.user_list_Filtered =user_list;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);
        context = parent.getContext();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchUserRecyclerViewAdapter.ViewHolder holder, final int position) {

        final String current_user_id = mAuth.getCurrentUser().getUid();
        final String user_id = user_list.get(position).getUser_id();
        final String user_name = user_list.get(position).getUsername();
        final String thumb_image = user_list.get(position).getThumb_image();
        holder.setUserData(user_name , thumb_image);
        if (current_user_id.equals(user_id)) {
            holder.mProfile_follow_btn.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.mProfile_follow_btn.setVisibility(View.VISIBLE);
        }
        holder.mProfile_follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!user_id.equals(current_user_id)) {
                    Intent profileIntent = new Intent(context, vProfileActivity.class);
                    profileIntent.putExtra("user_id", user_id);
                    context.startActivity(profileIntent);
                }
            }
        });
        fStore.collection("users/" + current_user_id + "/Followers/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        fStore.collection("users/" + current_user_id + "/user/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        holder.mProfile_follow_btn.setText(context.getString(R.string.unfollow));
                                        holder.mCurrent_state = "mutual_followers";
                                    } else {
                                        holder.mProfile_follow_btn.setText(context.getString(R.string.follow_back));
                                        holder.mCurrent_state = "single_followers";
                                    }
                                }
                            }
                        });

                    } else
                        fStore.collection("users/" + current_user_id + "/user/").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        holder.mProfile_follow_btn.setText(context.getString(R.string.unfollow));
                                        holder.mCurrent_state = "single_user";
                                    } else {
                                        holder.mProfile_follow_btn.setText(context.getString(R.string.follow));
                                        holder.mCurrent_state = "not_followers";
                                    }
                                }
                            }
                        });

                } else {
                    String e = task.getException().getMessage();
                }
            }
        });
        holder.mProfile_follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.SetFollowStatue(current_user_id, user_id);
            }
        });


    }
    @Override
    public int getItemCount() {
        return user_list_Filtered.size();
    }

    public List<User> getuser_list_Filtered() {
        return user_list_Filtered;
    }

    public void setuser_list_Filtered(List<User> user_list_Filtered) {
        this.user_list_Filtered = user_list_Filtered;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
//                Log.d(TAG_CONTACTS_SEARCH, "ContactListAdapter.getFilter.performFiltering: " +
//                        "charString == " + charString);
                if (charString.isEmpty()) {
                    user_list_Filtered = user_list;
                } else {
                    List<User> filteredList = new CopyOnWriteArrayList<>();
                    for (User row : user_list) {
                        // search on the user fullname
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    user_list_Filtered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = user_list_Filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                user_list_Filtered = (CopyOnWriteArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private CircleImageView mProfile_image;
        private TextView mProfile_user_name;
        private TextView mProfile_name;
        private Button mProfile_follow_btn;
        private String mCurrent_state;
        public ViewHolder(View itemView){
            super(itemView);
            view=itemView;
            mProfile_image = (CircleImageView)view.findViewById(R.id.user_image);
            mProfile_user_name = (TextView)view.findViewById(R.id.user_username);
            mProfile_name = (TextView)view.findViewById(R.id.user_name);
            mProfile_follow_btn = (Button) view.findViewById(R.id.user_btn);
            mCurrent_state = "not_followers";
        }

        public void setUserData(String username, String image){

            mProfile_user_name.setText(username);

            Glide.with(context).load(image).into(mProfile_image);

        }
        private void SetFollowStatue(String current_user_id ,String user_id )
        {
            if(mCurrent_state.equals("not_followers"))
            {
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                HashMap<String, Object> followersMap_ = new HashMap<>();
                followersMap_.put("time_stamp", timestamp.getTime());
                followersMap_.put("user_id", current_user_id);
                //
                HashMap<String, Object> userMap_ = new HashMap<>();
                userMap_.put("time_stamp", timestamp.getTime());
                userMap_.put("user_id", user_id);
                fStore.collection("users/" + user_id + "/Followers/").document(current_user_id).set(followersMap_);
                fStore.collection("users/" + current_user_id + "/user/").document(user_id).set(userMap_);
                mCurrent_state = "single_user";
                mProfile_follow_btn.setText(context.getString(R.string.unfollow));
            }
            // --------------- SINGLE user STATE ------------
            else if(mCurrent_state.equals("single_user"))
            {

                fStore.collection("users/" + current_user_id + "/user/").document(user_id).delete();
                fStore.collection("users/" + user_id + "/Followers/").document(current_user_id).delete();
                mCurrent_state = "not_followers";
                mProfile_follow_btn.setText(context.getString(R.string.follow));
            }
            // --------------- SINGLE FOLLOWERS STATE ------------
            else if(mCurrent_state.equals("single_followers"))
            {
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                HashMap<String, Object> followersMap_ = new HashMap<>();
                followersMap_.put("time_stamp", timestamp.getTime());
                followersMap_.put("user_id", current_user_id);
                //
                HashMap<String, Object> userMap_ = new HashMap<>();
                userMap_.put("time_stamp", timestamp.getTime());
                userMap_.put("user_id", user_id);
                fStore.collection("users/" + user_id + "/Followers/").document(current_user_id).set(followersMap_);
                fStore.collection("users/" + current_user_id + "/user/").document(user_id).set(userMap_);
                mCurrent_state = "mutual_followers";
                mProfile_follow_btn.setText(context.getString(R.string.unfollow));
            }
            // --------------- MUTUAL FOLLOWERS STATE ------------
            else if(mCurrent_state.equals("mutual_followers"))
            {
                fStore.collection("users/" + current_user_id + "/user/").document(user_id).delete();
                fStore.collection("users/" + user_id + "/Followers/").document(current_user_id).delete();
                mCurrent_state = "single_followers";
                mProfile_follow_btn.setText(context.getString(R.string.follow_back));
            }
        }
    }
}