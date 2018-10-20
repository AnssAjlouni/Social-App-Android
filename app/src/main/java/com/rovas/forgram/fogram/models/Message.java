package com.rovas.forgram.fogram.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/**
 * Created by Mohamed El Sayed
 */
public class Message extends com.rovas.forgram.fogram.models.ids.MessagesID implements Serializable, Cloneable {

	//public static final String TABLE_NAME = "messages";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_FROM_NAME = "from_name";
	public static final String COLUMN_MESSAGE= "message";
	public static final String COLUMN_MEDIA_PATH= "media_path";
	//public static final String COLUMN_MEDIA_COLLECTION= "media_collection";
	public static final String COLUMN_MEDIA_URL= "media_url";
	public static final String COLUMN_TIME_STAMP= "time_stamp";
	public static final String COLUMN_SEEN= "seen";
	public static final String COLUMN_POSITION= "position";
	public static String CREATE_TABLE(String TABLE_NAME)
	{
		return
			"CREATE TABLE " + TABLE_NAME + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_FROM_NAME +" TEXT ,"
			+ COLUMN_MESSAGE +" TEXT ,"
			+ COLUMN_MEDIA_URL +" BLOB,"
			+ COLUMN_POSITION +" TEXT,"
			+ COLUMN_MEDIA_PATH +" TEXT,"
			+ COLUMN_TIME_STAMP +" INTEGER,"
			+ COLUMN_SEEN +" VARCHAR"
			+ ")";
	}
	public static String CREATE_TABLE_EXIST(String TABLE_NAME)
	{
		return
				"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_FROM_NAME +" TEXT ,"
				+ COLUMN_MESSAGE +" TEXT ,"
				+ COLUMN_MEDIA_URL +" BLOB,"
				+ COLUMN_POSITION +" TEXT,"
				+ COLUMN_MEDIA_PATH +" TEXT,"
				+ COLUMN_TIME_STAMP +" INTEGER,"
				+ COLUMN_SEEN +" VARCHAR"
				+ ")";
	}

	private int id;
	private String from_name, message;
	private byte[] media_url;
	private String media_path;
	private HashMap<String , String> media_collection;
	private long time_stamp;
	private boolean seen;
	private String position;
	private String from;
	private List<Message> messages;
	public List<Message> getMessages() {
		return messages;
	}
	//
	//
	public Message(int conversationId, String participantName,
						List<Message> messages) {
		this.id = conversationId;
		this.from_name = participantName;
		this.messages = messages == null ? Collections.<Message>emptyList() : messages;
		this.time_stamp = System.currentTimeMillis();
	}

	public Message() {
	}
	public Message(int id , String from_name, String message, String position, String media_path , long time_stamp , boolean seen) {
		this.id = id;
		this.from_name = from_name;
		this.message = message;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;

	}
	public Message(int id , String from_name, String message , byte[] media_url, String position, String media_path , long time_stamp , boolean seen) {
		this.id = id;
		this.from_name = from_name;
		this.message = message;
		this.media_url = media_url;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;
	}
	public Message(String from_name, String message, String position, String media_path , long time_stamp , boolean seen) {
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

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String toString() {
		return "[Conversation: conversationId=" + id +
				", participantName=" + from_name +
				", messages=" + messages +
				", timestamp=" + time_stamp + "]";
	}



}
