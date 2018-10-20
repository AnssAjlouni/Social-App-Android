package com.rovas.forgram.fogram.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * Created by Mohamed El Sayed
 */
public class MessageGroup extends com.rovas.forgram.fogram.models.ids.MessagesID  implements Serializable, Cloneable{

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
		return"CREATE TABLE " + TABLE_NAME + "("
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
		return  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
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
	private CopyOnWriteArrayList<User_Seen> contacts = new CopyOnWriteArrayList<>(); // contacts in memory
	private int id;
	private String from_name, message;
	private byte[] media_url;
	private String media_path;
	private HashMap<String , String> media_collection;
	private long time_stamp;
	private boolean seen;
	private Map<String, Boolean> members;
	private String position;
	private String from;
	private List<MessageGroup> messages;
	private Map<String ,String> members_seen;
	public List<MessageGroup> getMessages() {
		return messages;
	}
	//
	//
	public MessageGroup(int conversationId, String participantName,
                        List<MessageGroup> messages) {
		this.id = conversationId;
		this.from_name = participantName;
		this.messages = messages == null ? Collections.<MessageGroup>emptyList() : messages;
		this.time_stamp = System.currentTimeMillis();
	}

	public MessageGroup() {
	}
	public MessageGroup(int id , String from_name, String message, String position, String media_path , long time_stamp , boolean seen) {
		this.id = id;
		this.from_name = from_name;
		this.message = message;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;

	}
	public MessageGroup(int id , String from_name, String message , byte[] media_url, String position, String media_path , long time_stamp , boolean seen) {
		this.id = id;
		this.from_name = from_name;
		this.message = message;
		this.media_url = media_url;
		this.position = position;
		this.media_path=media_path;
		this.time_stamp = time_stamp;
		this.seen = seen;
	}
	public MessageGroup(String from_name, String message, String position, String media_path , long time_stamp , boolean seen) {
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
	public User_Seen findById(String contactId) {
        /*
        Iterator<Map.Entry<String, Integer>> entries = getMembers().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Integer> entry = entries.next();
            if (contactId.equals(entry.getValue())) {
                return entry.getValue();
            }
            Log.d(TAG, "findById: Key = " + entry.getKey() + ", Value = " + entry.getValue() );
        }
        */
		//
		for (User_Seen contact : contacts ) {//getMembers().entrySet()
			if (contact.getUser_id().equals(contactId)) {
				return contact;
			}
		}

		return null;
	}
	private List<User_Seen> patchMembers(Map<String, Boolean> members) {
		List<User_Seen> patchedMembers = new ArrayList<>();

		for (Map.Entry<String, Boolean> entry : members.entrySet()) {
			User_Seen contact = findById(entry.getKey());
			if (contact != null) {
				patchedMembers.add(contact);
			} else {
				// add user id
				// TODO: 30/04/18 hardcoded username "system"
//                Log.d("entry", entry.toString());
				if(!entry.getKey().equals("system")) {
					patchedMembers.add(new User_Seen(entry.getKey(), false));
				}
			}

			// TODO: 07/02/18 check for this
			//TODO ADD CURRENT_USER
            /*
            if (entry.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {//TODO CURRENT_USER_ID
                if (!patchedMembers.contains(FirebaseAuth.getInstance().getCurrentUser())) {
                    patchedMembers.add(FirebaseAuth.getInstance().getCurrentUser());
                }
            }
            */
		}

		return patchedMembers;
	}

	public String printMembersListWithSeparator(String separator) {
		String delimitedList = "";
		String usersWithoutDisplayName = "";

		if (getMembersList() != null && getMembersList().size() > 0) {
			// append chat users
			Iterator<User_Seen> it = getMembersList().iterator();

			while (it.hasNext()) {
				User_Seen usr = it.next();
				String userId = usr.getUser_id();
				//TODO

				Boolean seen = usr.getSeen();

				if(seen)
				{
					delimitedList += separator + "seen";
				}
				else
				{
					usersWithoutDisplayName += separator + "false";
				}

			}

			// append the list of the user without the fullName to the list of the user with a
			// valid fullName
			delimitedList = delimitedList + usersWithoutDisplayName;

			// if the string starts with separator remove it
			if (delimitedList.startsWith(separator)) {
				delimitedList = delimitedList.replaceFirst("^" + separator, "");
			}
		}

		return delimitedList;
	}

	public Map<String, Boolean> getMembers() {
		return members;
	}

	public void setMembers(Map<String, Boolean> members) {
		this.members = members;
	}

	public void setMessages(List<MessageGroup> messages) {
		this.messages = messages;
	}

	public Map<String, String> getMembers_seen() {
		return members_seen;
	}

	public void setMembers_seen(Map<String, String> members_seen) {
		this.members_seen = members_seen;
	}

	@Exclude
	public List<User_Seen> getMembersList() {

		return patchMembers(members);
	}
	public String toString() {
		return "[Conversation: conversationId=" + id +
				", participantName=" + from_name +
				", messages=" + messages +
				", timestamp=" + time_stamp + "]";
	}



}
