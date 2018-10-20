package com.rovas.forgram.fogram.models;

/**
 * Created by Mohamed El Sayed
 */
public class Followers extends com.rovas.forgram.fogram.models.ids.FollowersID {


    private long time_stamp;
    private String user_id;
    private String thumb_image;
    private String username;

    public Followers(){

    }

    public Followers(String user_id , long time_stamp) {
        this.user_id = user_id;
        this.time_stamp = time_stamp;

    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}