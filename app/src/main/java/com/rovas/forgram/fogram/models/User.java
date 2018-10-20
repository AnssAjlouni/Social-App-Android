package com.rovas.forgram.fogram.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.rovas.forgram.fogram.exception.ChatUserIdException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mohamed El Sayed
 */

public class User extends com.rovas.forgram.fogram.models.ids.UserID implements Serializable ,Parcelable , Cloneable {
    private List<Notification> notificationList;
    private List<Conversation> chatList;
    private User user_data;
    private String token_id;
    private String user_id;
    private long phone_number;
    private String name;
    private String email;
    private String username;
    private long role;
    private long birth_date;
    private long created_date;
    private String device_token;
    private String image;
    private String status;
    private String thumb_image;
    private long last_seen;
    private long followers;
    private long following;
    private long posts;

    public User()
    {

    }
    public User(String user_id, String username) {

        if (user_id.contains(".")){
            throw new ChatUserIdException("Id Field contains invalid char");
        }

        this.user_id = user_id;
        this.username = username;
    }
    public User(long last_seen) {
        this.last_seen = last_seen;
    }

    public User(String user_id, String username , String name , String thumb_image) {
        this.user_id = user_id;
        this.username = username;
        this.thumb_image = thumb_image;
        this.name = name;
    }
    public User(String user_id,String image , String name) {
        this.user_id = user_id;
        this.image = image;
        this.name = name;
    }
    public User(String user_id, long phone_number, String email, String username ) {
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.email = email;
        this.username = username;
    }


    public User(String user_id, long phone_number, String email, String username , long birth_date
            , long created_date ,String device_token , String image , String status , String thumb_image , String name , long followers,
                long following, long posts) {
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.email = email;
        this.username = username;
        this.name = name;
        this.birth_date = birth_date;
        this.created_date = created_date;
        this.device_token = device_token;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;
        this.followers = followers;
        this.following = following;
        this.posts = posts;

    }



    protected User(Parcel in) {
        user_id = in.readString();
        phone_number = in.readLong();
        email = in.readString();
        username = in.readString();
        name = in.readString();
        birth_date = in.readLong();
        created_date = in.readLong();
        device_token = in.readString();
        image = in.readString();
        status = in.readString();
        thumb_image = in.readString();
        followers = in.readLong();
        following = in.readLong();
        posts = in.readLong();
        last_seen = in.readLong();
        role = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public long getRole() {
        return role;
    }

    public void setRole(long role) {
        this.role = role;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }
    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(long birth_date) {
        this.birth_date = birth_date;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", email='" + email + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", created_date='" + created_date + '\'' +
                ", device_token='" + device_token + '\'' +
                ", image='" + image + '\'' +
                ", status='" + status + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", role=" + role +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public User WithId(String id){
        this.user_id = id;
        return this;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeLong(phone_number);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeLong(birth_date);
        dest.writeLong(created_date);
        dest.writeString(device_token);
        dest.writeString(image);
        dest.writeString(status);
        dest.writeString(thumb_image);
        dest.writeString(name);
        dest.writeLong(followers);
        dest.writeLong(following);
        dest.writeLong(posts);
        dest.writeLong(last_seen);
        dest.writeLong(role);
    }

    public User getUser_data() {
        return user_data;
    }

    public void setUser_data(User user_data) {
        this.user_data = user_data;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }
    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public List<Conversation> getChatList() {
        return chatList;
    }

    public void setChatList(List<Conversation> chatList) {
        this.chatList = chatList;
    }
}
