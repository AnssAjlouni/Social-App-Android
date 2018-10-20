package com.rovas.forgram.fogram.models;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.rovas.forgram.fogram.Utils.StringUtils;
import com.rovas.forgram.fogram.models.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Mohamed El Sayed
 */

public class ChatGroup extends com.rovas.forgram.fogram.models.ids.GroupID implements Serializable, Cloneable {
    private static final String TAG = "ChatGroup" ;
    private CopyOnWriteArrayList<User> contacts = new CopyOnWriteArrayList<>(); // contacts in memory
    @Exclude
    private String groupId;
    private long createdOnLong;
    //private Long createdOn;
    private String iconURL;
    private Map<String, String> members;
    private String name;
    private String owner;
    public ChatGroup() {
        members = new HashMap<>();
    }

//    public ChatGroup(String name, String owner) {
//        this.name = name;
//        this.owner = owner;
//        members = new HashMap<>();
//    }

    @Exclude
    public String getGroupId() {
        return groupId;
    }

    @Exclude
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    //TODO
    /*
    public Map<String, String> getCreatedOn() {
        return ServerValue.TIMESTAMP;
    }
    */
    public void setTimestamp(Long createdOnLong) {
        this.createdOnLong = createdOnLong;
    }

    @Exclude
    public Long getCreatedOnLong() {
        return createdOnLong;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public void addMembers(Map<String, String> members) {
        this.members.putAll(members);
    }

    public void addMember(String member) {
        this.members.put(member, "1");
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    public User findById(String contactId) {
        /*
        Iterator<Map.Entry<String, Integer>> entries = getMembers().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Integer> entry = entries.next();
            if (contactId.equals(entry.getValue())) {
                return entry.getValue();
            }
            Log.d(TAG, "findById: Key = " + entry.getKey() + ", Value = " + entry.getValue() );
        }
        */
        //
        for (User contact : contacts ) {//getMembers().entrySet()
            if (contact.getUser_id().equals(contactId)) {
                return contact;
            }
        }

        return null;
    }

    @Exclude
    public List<User> getMembersList() {

        return patchMembers(members);
    }
    public String printMembersListWithSeparator(String separator) {
        String delimitedList = "";
        String usersWithoutDisplayName = "";

        if (getMembersList() != null && getMembersList().size() > 0) {
            // append chat users
            Iterator<User> it = getMembersList().iterator();

            while (it.hasNext()) {
                User usr = it.next();
                String userId = usr.getUser_id();
                //TODO

                String fullName = usr.getUsername();
                Log.d(TAG, "printMembersListWithSeparator: " + usr.getUsername());
                if (StringUtils.isValid(fullName)) {
                    delimitedList += separator + fullName;
                } else {
                    usersWithoutDisplayName += separator + userId;
                }

            }

            // append the list of the user without the fullName to the list of the user with a
            // valid fullName
            delimitedList = delimitedList + usersWithoutDisplayName;

            // if the string starts with separator remove it
            if (delimitedList.startsWith(separator)) {
                delimitedList = delimitedList.replaceFirst("^" + separator, "");
            }
        }

        return delimitedList;
    }
    private List<User> patchMembers(Map<String, String> members) {
        List<User> patchedMembers = new ArrayList<>();

        for (Map.Entry<String, String> entry : members.entrySet()) {
            User contact = findById(entry.getKey());
            if (contact != null) {
                patchedMembers.add(contact);
            } else {
                // add user id
                // TODO: 30/04/18 hardcoded username "system"
//                Log.d("entry", entry.toString());
                if(!entry.getKey().equals("system")) {
                    patchedMembers.add(new User(entry.getKey(), entry.getValue()));
                }
            }

            // TODO: 07/02/18 check for this
            //TODO ADD CURRENT_USER
            /*
            if (entry.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {//TODO CURRENT_USER_ID
                if (!patchedMembers.contains(FirebaseAuth.getInstance().getCurrentUser())) {
                    patchedMembers.add(FirebaseAuth.getInstance().getCurrentUser());
                }
            }
            */
        }

        return patchedMembers;
    }
    @Override
    public String toString() {
        return "ChatGroup{" +
                "groupId='" + groupId + '\'' +
                ", createdOnLong=" + createdOnLong +
                ", iconURL='" + iconURL + '\'' +
                ", members=" + members +
                ", name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }
}
