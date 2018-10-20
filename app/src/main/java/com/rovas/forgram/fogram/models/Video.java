package com.rovas.forgram.fogram.models;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.Date;
import java.util.List;

/**
 * Created by Mohamed El Sayed
 */
public class Video implements Parcelable {

    private String desc;
    private long time_stamp;
    private String video_id;
    private String video_url;
    private String thumb;
    private String user_id;
    private String tags;
    private String post_type;
    private String text_post;
    private String thumb_image;
    private String username;


    public Video() {

    }

    public Video(String desc, long time_stamp, String video_url, String video_id,
                 String user_id, String tags, String post_type, String text_post , String username , String thumb_image) {
        this.desc = desc;
        this.time_stamp = time_stamp;
        this.video_url = video_url;
        this.video_id = video_id;
        this.user_id = user_id;
        this.tags = tags;
        this.post_type = post_type;
        this.text_post = text_post;
        this.username = username;
        this.thumb_image = thumb_image;
    }

    protected Video(Parcel in) {
        desc = in.readString();
        time_stamp = in.readLong();
        video_url = in.readString();
        video_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
        post_type = in.readString();
        text_post = in.readString();
        thumb_image = in.readString();
        username = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(desc);
        dest.writeLong(time_stamp);
        dest.writeString(video_url);
        dest.writeString(video_id);
        dest.writeString(user_id);
        dest.writeString(tags);
        dest.writeString(post_type);
        dest.writeString(text_post);
        dest.writeString(thumb_image);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };



    public static Creator<Video> getCREATOR() {
        return CREATOR;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
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

    @Override
    public String toString() {
        return "BlogPost{" +
                "desc='" + desc + '\'' +
                ", time_stamp='" + time_stamp + '\'' +
                ", video_url='" + video_url + '\'' +
                ", video_id='" + video_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_type='" + post_type + '\'' +
                ", text_post='" + text_post + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", username='" + username + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
