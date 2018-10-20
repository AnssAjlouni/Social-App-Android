package com.rovas.forgram.fogram.models;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;
import com.rovas.forgram.fogram.models.ids.MessagesID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Mohamed El Sayed
 */
public class Messages extends com.rovas.forgram.fogram.models.ids.MessagesID{

    private String message, type;
    private long  time;
    private boolean seen;
    private String position;
    private String from;
    private String media_url;
    private String media_thumb_uri;
    private HashMap<String , String> media_collection;

    public String getMedia_thumb_uri() {
        return media_thumb_uri;
    }

    public void setMedia_thumb_uri(String media_thumb_uri) {
        this.media_thumb_uri = media_thumb_uri;
    }

    public HashMap<String, String> getMedia_collection() {
        return media_collection;
    }

    public void setMedia_collection(HashMap<String, String> media_collection) {
        this.media_collection = media_collection;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public Messages(String message ,String type , String position , String from , long time , boolean seen )
    {
        this.message = message;
        this.type = type;
        this.position = position;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }
    public Messages(String message, String type, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
    /*
    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        //String message , List<Uri> media_collection ,String type , String position , String from , long time , boolean seen )
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("media_collection", media_collection);
        result.put("type", type);
        result.put("position", position);
        result.put("from", from);
        result.put("time", time);
        result.put("seen", seen);
        return result;
    }
    */
    public Messages(){

    }

}