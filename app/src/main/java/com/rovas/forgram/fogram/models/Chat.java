package com.rovas.forgram.fogram.models;

import com.rovas.forgram.fogram.models.ids.ChatID;
/**
 * Created by Mohamed El Sayed
 */
public class Chat extends com.rovas.forgram.fogram.models.ids.ChatID{
    public boolean seen;
    public long timestamp;
    public String message;
    public String type;
    public String user_id;
    public String username;
    public String thumb_image;
    public String ownder_id;

    public String getOwnder_id() {
        return ownder_id;
    }

    public void setOwnder_id(String ownder_id) {
        this.ownder_id = ownder_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chat(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Chat(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }
}
