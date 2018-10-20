package com.rovas.forgram.fogram.models;


import java.util.Date;
/**
 * Created by Mohamed El Sayed
 */
public class StoriesPost extends com.rovas.forgram.fogram.models.ids.StoriesPostID {
    public String user_id , image_url , length ,likers , username ,thumb_image ,post_type ,text_post ;
    public long time_stamp;

    public StoriesPost()
    {

    }

    public String getText_post() {
        return text_post;
    }

    public void setText_post(String text_post) {
        this.text_post = text_post;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
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

    public StoriesPost(String user_id, String image_url, String length, String thumb_image, String likers, long time_stamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.length = length;
        this.thumb_image = thumb_image;
        this.likers = likers;
        this.time_stamp = time_stamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLikers() {
        return likers;
    }

    public void setLikers(String likers) {
        this.likers = likers;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }





}
