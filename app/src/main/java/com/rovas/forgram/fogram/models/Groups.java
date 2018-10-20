package com.rovas.forgram.fogram.models;

import java.util.HashMap;
/**
 * Created by Mohamed El Sayed
 */
public class Groups extends com.rovas.forgram.fogram.models.ids.GroupID {

    private long createdOnLong;
    private String groupId;
    private String iconURL;
    private HashMap<String , Object> members;
    private String name;
    private String owner;
    private String message;
    public boolean seen;
    public Groups()
    {

    }
    public Groups(long createdOnLong, String groupId, String iconURL, HashMap<String, Object> members, String name, String owner) {
        this.createdOnLong = createdOnLong;
        this.groupId = groupId;
        this.iconURL = iconURL;
        this.members = members;
        this.name = name;
        this.owner = owner;
    }

    public long getCreatedOnLong() {
        return createdOnLong;
    }

    public void setCreatedOnLong(long createdOnLong) {
        this.createdOnLong = createdOnLong;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public HashMap<String, Object> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Object> members) {
        this.members = members;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
