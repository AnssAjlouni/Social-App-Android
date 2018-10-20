package com.rovas.forgram.fogram.models;

import java.util.HashMap;
/**
 * Created by Mohamed El Sayed
 */
public class UnreadMessage extends com.rovas.forgram.fogram.models.ids.MessagesID {


	private int id;
	private String from_name, message;
	private byte[] media_url;
	private String media_path;
	private HashMap<String , String> media_collection;
	private long time_stamp;
	private boolean seen;
	private String position;

	public UnreadMessage() {
	}
	public UnreadMessage(int id , String from_name, String message, String position, String media_path , long time_stamp , boolean seen) {
		this.id = id;
		this.from_name = from_name;
		this.message = message;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;

	}
	public UnreadMessage(int id , String from_name, String message , byte[] media_url, String position, String media_path , long time_stamp , boolean seen) {
		this.id = id;
		this.from_name = from_name;
		this.message = message;
		this.media_url = media_url;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;
	}
	public UnreadMessage(String from_name, String message, String position, String media_path , long time_stamp , boolean seen) {
		this.from_name = from_name;
		this.message = message;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMedia_path() {
		return media_path;
	}

	public void setMedia_path(String media_path) {
		this.media_path = media_path;
	}

	public HashMap<String, String> getMedia_collection() {
		return media_collection;
	}

	public void setMedia_collection(HashMap<String, String> media_collection) {
		this.media_collection = media_collection;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public long getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(long time_stamp) {
		this.time_stamp = time_stamp;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public byte[] getMedia_url() {
		return media_url;
	}

	public void setMedia_url(byte[] media_url) {
		this.media_url = media_url;
	}



	public String getFrom_name() {
		return from_name;
	}

	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String toString() {
		return "[Conversation: conversationId=" + id +
				", participantName=" + from_name +
				", messages=" + message +
				", timestamp=" + time_stamp + "]";
	}



}
