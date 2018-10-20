package com.rovas.forgram.fogram.models;

import com.rovas.forgram.fogram.exception.ChatUserIdException;
/**
 * Created by Mohamed El Sayed
 */
public class User_Seen {
    private Boolean seen;
    private String user_id;

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public User_Seen(String user_id, Boolean seen) {

        if (user_id.contains(".")){
            throw new ChatUserIdException("Id Field contains invalid char");
        }

        this.user_id = user_id;
        this.seen = seen;
    }
}
