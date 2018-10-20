package com.rovas.forgram.fogram.views;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.StringUtils;
import com.rovas.forgram.fogram.base.AbstractRecyclerAdapter;
import com.rovas.forgram.fogram.interfaces.OnGroupMemberClickListener;
import com.rovas.forgram.fogram.managers.UserWiazrd;
import com.rovas.forgram.fogram.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed El Sayed
 */
public class GroupMembersListAdapter extends AbstractRecyclerAdapter<User,
        GroupMembersListAdapter.ViewHolder> {
    private static final String TAG = "GroupMembersListAdapter";
    private FirebaseAuth mAuth;
    private String current_user_id;
    private List<User> admins;

    private OnGroupMemberClickListener onGroupMemberClickListener;

    public GroupMembersListAdapter(Context context, List<User> mList) {
        super(context, mList);

        admins = new ArrayList<>();
    }

    public OnGroupMemberClickListener getOnGroupMemberClickListener() {
        return onGroupMemberClickListener;
    }

    public void setOnGroupMemberClickListener(OnGroupMemberClickListener onGroupMemberClickListener) {
        this.onGroupMemberClickListener = onGroupMemberClickListener;
    }

    @Override
    public GroupMembersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.t_row_group_members, parent, false);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupMembersListAdapter.ViewHolder holder, final int position) {
//        Log.d(TAG, "onBindViewHolder");
        User contact = getItem(position);

        holder.contact.setText(StringUtils.isValid(contact.getUsername()) ? contact.getUsername() : contact.getUser_id());

        loadProfileImage(holder, contact);

        showAdminLabel(holder, contact);

        setOnMemberClickListener(holder, position, contact);
    }

    // if the contact is an admin it shows the admin label near the name
    private void showAdminLabel(ViewHolder holder, User contact) {
        if (admins.contains(contact)) {
            //Log.d(TAG, "addAdmin:    if (admins.contains(contact)) {");
            holder.mGroupAdminLabel.setVisibility(View.VISIBLE);
        } else {
                holder.mGroupAdminLabel.setVisibility(View.GONE);
        }
    }

    // load the current contact profile image
    private void loadProfileImage(ViewHolder holder, User contact) {
//        Log.d(TAG, "loadProfileImage");

        String url = contact.getThumb_image();

        if(url != null) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .into(holder.profilePicture);
        }
    }

    // handle the click on a member
    private void setOnMemberClickListener(ViewHolder holder,
                                          final int position, final User contact) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnGroupMemberClickListener().onGroupMemberClicked(contact, position);
            }
        });
    }

    /**
     * Add a group admin if isn't already added
     *
     * @param admin
     */
    public void addAdmin(User admin) {
        if (!admins.contains(admin)) {
            Log.d(TAG, "addAdmin:  if (!admins.contains(admin)) {");
            admins.add(admin);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView contact;
        private final ImageView profilePicture;
        private final TextView mGroupAdminLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            contact = (TextView) itemView.findViewById(R.id.recipient_display_name);
            profilePicture = (ImageView) itemView.findViewById(R.id.recipient_picture);
            mGroupAdminLabel = (TextView) itemView.findViewById(R.id.label_admin);
        }
    }
}