package com.rovas.forgram.fogram.models;

import java.util.Date;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class Notification extends com.rovas.forgram.fogram.models.ids.NotificationID {

    private List<Notification> notificationList;
    String from="",message="";
    private String thumb_image;
    private long timestamp;
    private String sender_name;
    private String post;
    private String type;
    private String forward;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public Notification(){

    }

    public Notification(String from, String messaege , String thumb_image , String sender_name
            , long timestamp ,String post ,String type , String forward) {
        this.from = from;
        this.message = messaege;
        this.thumb_image = thumb_image;
        this.timestamp = timestamp;
        this.sender_name = sender_name;
        this.post = post;
        this.type = type;
        this.forward = forward;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}