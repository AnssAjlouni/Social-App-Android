package com.rovas.forgram.fogram.views;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.Utils.StringUtils;
import com.rovas.forgram.fogram.interfaces.OnContactClickListener;
import com.rovas.forgram.fogram.models.Following;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohamed El Sayed
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = "ContactListAdapter" ;
    private List<Following> contactList;

    private List<Following> contactListFiltered;

    private OnContactClickListener onContactClickListener;
    public ContactListAdapter(List<Following> contactList) {
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    public void setList(List<Following> list) {
        this.contactList = list;
    }

    public void setOnContactClickListener(OnContactClickListener onContactClickListener) {
        this.onContactClickListener = onContactClickListener;
    }

    public OnContactClickListener getOnContactClickListener() {
        return onContactClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.t_row_contact_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.ViewHolder holder, final int position) {
        final Following contact = contactListFiltered.get(position);
        holder.mContactFullName.setText(StringUtils.isValid(contact.getUsername()) ?
                contact.getUsername() : contact.getUser_id());
        holder.mContactUsername.setText(contact.getUsername());
        if(contact.getThumb_image() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(contact.getThumb_image())
                    .into(holder.mProfilePicture);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnContactClickListener().onContactClicked(contact, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
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
                    contactListFiltered = contactList;
                } else {
                    List<Following> filteredList = new ArrayList<Following>();
                    for (Following row : contactList) {
                        // search on the user fullname
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //synchronizer = new Synchronizer();
                //contactListFiltered.clear();
                contactListFiltered.addAll((ArrayList<Following>) filterResults.values);
                //TODO
                //contactListFiltered = synchronizer.getFollowingList();
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mContactFullName;
        private final TextView mContactUsername;
        private final CircleImageView mProfilePicture;

        ViewHolder(View itemView) {
            super(itemView);
            mContactFullName = (TextView) itemView.findViewById(R.id.fullname);
            mContactUsername = (TextView) itemView.findViewById(R.id.username);
            mProfilePicture = (CircleImageView) itemView.findViewById(R.id.profile_picture);
        }
    }
}
