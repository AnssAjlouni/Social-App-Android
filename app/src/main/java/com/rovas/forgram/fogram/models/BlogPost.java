package com.rovas.forgram.fogram.models;

import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;

/**
 * Created by Mohamed El Sayed
 */
public class BlogPost extends com.rovas.forgram.fogram.models.ids.BlogPostID implements Serializable, Cloneable ,Parcelable {

    private long time_stamp;
    private String photo_id;
    private String image_url;
    private String thumb_image;
    private String username;
    private String user_id;
    private String tags;
    private String post_type;
    private String text_post;
    private int likes_count;
    private int comments_count;
    private String post;

    public BlogPost() {

    }

    public BlogPost(String desc, long time_stamp, String image_url, String photo_id,
                    String user_id, String tags, String post_type, String text_post , String username , String thumb_image) {
        this.time_stamp = time_stamp;
        this.image_url = image_url;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
        this.post_type = post_type;
        this.text_post = text_post;
        this.username = username;
        this.thumb_image = thumb_image;
    }

    protected BlogPost(Parcel in) {
        time_stamp = in.readLong();
        image_url = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
        post_type = in.readString();
        text_post = in.readString();
        thumb_image = in.readString();
        username = in.readString();
        post = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time_stamp);
        dest.writeString(image_url);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeString(tags);
        dest.writeString(post_type);
        dest.writeString(text_post);
        dest.writeString(thumb_image);
        dest.writeString(username);
        dest.writeString(post);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BlogPost> CREATOR = new Creator<BlogPost>() {
        @Override
        public BlogPost createFromParcel(Parcel in) {
            return new BlogPost(in);
        }

        @Override
        public BlogPost[] newArray(int size) {
            return new BlogPost[size];
        }
    };



    public static Creator<BlogPost> getCREATOR() {
        return CREATOR;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }
    public String getText_post() {
        return text_post;
    }

    public void setText_post(String text_post) {
        this.text_post = text_post;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "BlogPost{" +
                "time_stamp='" + time_stamp + '\'' +
                ", image_url='" + image_url + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_type='" + post_type + '\'' +
                ", text_post='" + text_post + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", username='" + username + '\'' +
                ", tags='" + tags + '\'' +
                ", post='" + post + '\'' +
                '}';
    }
}
