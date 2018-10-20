package com.rovas.forgram.fogram.managers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rovas.forgram.fogram.Utils.Message_Status;
import com.rovas.forgram.fogram.interfaces.ChatGroupCreatedListener;
import com.rovas.forgram.fogram.interfaces.ChatGroupsListener;
import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.models.Chat;
import com.rovas.forgram.fogram.models.Conversation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rovas.forgram.fogram.Utils.DebugConstants.DEBUG_GROUPS;
/**
 * Created by Mohamed El Sayed
 */
public class GroupBuilder {
    private List<ChatGroupsListener> chatGroupsListeners;
    private List<ChatGroup> chatGroups;
    private String currentUserId;
    public GroupBuilder(String currentUserId)
    {
        chatGroupsListeners = new ArrayList<>();
        chatGroups = new ArrayList<>(); // chatGroups in memory
        this.currentUserId = currentUserId;
    }
    public ChatGroup getById(String groupId) {
        for (ChatGroup chatGroup : chatGroups) {
            if (chatGroup.getGroupId().equals(groupId)) {
                return chatGroup;
            }
        }
        return null;
    }
    public void removeMemberFromChatGroup(ChatGroup chatGroup , String groupId, String toRemove_user_id) {
        int index = chatGroups.indexOf(chatGroup);
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        if (chatGroup.getMembers().containsKey(toRemove_user_id)) {
            // remove from firebase app reference
            //appGroupsNode.child("/" + groupId + "/members/" + toRemove.getGroupId()).removeValue();
            //toRemove_user_id
            // remove member from local group
            chatGroup.getMembers().remove(toRemove_user_id);
            //chatGroup.getMembersList().remove(toRemove_user_id);
            HashMap<String , Object> remove_member_update = new HashMap<>();
            remove_member_update.put("members" , chatGroup.getMembers());
            fStore.collection("groups").document(chatGroup.getGroupId()).update(remove_member_update);
            //Remove Chat DB to Removed Member
            //
            String user_ID = "" + toRemove_user_id + "";
            fStore.collection("chat").document(user_ID).collection("with").document(groupId).delete();
            //
            /*
            // update local chatGroups
            chatGroups.set(index, chatGroup);
            */
            // notify all subscribers
            if (chatGroupsListeners != null) {
                for (ChatGroupsListener chatGroupsListener : chatGroupsListeners) {
                    chatGroupsListener.onGroupChanged(chatGroup, null);
                }
            }
        }
    }

    public void addMembersToChatGroup(ChatGroup chatGroup , String groupId , String current_user_name, Map<String, String> toAdd) {
        Map<String, String> chatGroupMembers = chatGroup.getMembers();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        // add the news member to the existing members
        // the map automatically override existing keys
        chatGroupMembers.putAll(toAdd);
        //chatGroupMembers.put(currentUserId, current_user_name);
        HashMap<String , Object> add_member_update = new HashMap<>();
        add_member_update.put("members" , chatGroup.getMembers());
        fStore.collection("groups").document(chatGroup.getGroupId()).update(add_member_update);
        //Add Chat DB to New Members
        //
        final Conversation chat = createUserGroupForFirebase( chatGroup, chatGroup.getName()  , groupId, "Future-System" , chatGroup.getIconURL());
        for (Map.Entry<String, String> entry : toAdd.entrySet()) {
                String user_ID = "" + entry.getKey() + "";
                fStore.collection("chat").document(user_ID).collection("with").document(groupId).set(chat);
        }
        //
        //appGroupsNode.child("/" + chatGroup.getGroupId() + "/members/").setValue(chatGroupMembers);
        //fStore.collection("groups").document(chatGroup.getGroupId()).collection("members").add(chatGroupMembers);
    }
    public void createChatGroup(final String chatGroupName, Map<String, String> members, long _groupID ,String ImgURL,
                                final ChatGroupCreatedListener chatGroupCreatedListener) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        //String newGroupId = newGroupReference.getKey();
        final ChatGroup chatGroup = createGroupForFirebase(chatGroupName , ImgURL, members);
        final String groupID = "" +_groupID + "";
        chatGroup.setGroupId(_groupID + "");
        //TODO
        fStore.collection("groups").document(groupID).set(chatGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    //saveOrUpdateGroupInMemory(chatGroup);
                    //TODO
                    if (chatGroupCreatedListener != null) {
                        chatGroupCreatedListener.onChatGroupCreated(chatGroup, null);
                    }

                }
                else
                {
                    //TODO EXCEPTION
                    if (chatGroupCreatedListener != null) {
                        chatGroupCreatedListener.onChatGroupCreated(null,
                                null);
                    }
                }
            }
        });
        final Conversation chat = createUserGroupForFirebase( chatGroup, chatGroupName  , groupID, "Future-System" , ImgURL);
        final Conversation chat_owner = createOwnerGroupForFirebase(chatGroup , chatGroupName , groupID , ImgURL);
        for (Map.Entry<String, String> entry : members.entrySet()) {
            if(chat_owner.getUser_id().equals(currentUserId))
            {
                fStore.collection("chat").document(currentUserId).collection("with").document(groupID).set(chat_owner);
            }
            else {
                String user_ID = "" + entry.getKey() + "";
                fStore.collection("chat").document(user_ID).collection("with").document(groupID).set(chat);
            }
        }
        /*
        for(int x = 0 ; x < members.size() ; ++x)
        {
            String user_ID = ""+ members.get(x) + "";
            fStore.collection("chat").document(user_ID).collection("with").document(groupID).set(chatGroup);
        }
        */
    }

    private ChatGroup createGroupForFirebase(String chatGroupName ,String ImgURL, Map<String, String> members) {
        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setName(chatGroupName);
        chatGroup.setIconURL(ImgURL);
        chatGroup.setMembers(members);
        chatGroup.setOwner(currentUserId);
        chatGroup.setTimestamp(new Date().getTime());
        return chatGroup;
    }
    private Conversation createUserGroupForFirebase(ChatGroup chatGroup , String chatGroupName ,String group_Id, String Owner_Name , String ImgUrl) {
        Conversation conversation = new Conversation();
        conversation.setMessage(Owner_Name + " Created group '" + chatGroupName + "'");
        conversation.setChannelType(Message_Status.GROUP_CHANNEL_TYPE);
        conversation.setTime_stamp(chatGroup.getCreatedOnLong());
        conversation.setFrom_name(chatGroup.getName());
        conversation.setFrom_id(chatGroup.getGroupId());
        conversation.setThumb_image(ImgUrl);
        conversation.setFrom_name(chatGroup.getName());
        conversation.setUser_id(group_Id);
        return conversation;
    }
    private Conversation createOwnerGroupForFirebase(ChatGroup chatGroup , String chatGroupName ,String group_Id , String ImgUrl) {
        Conversation conversation = new Conversation();
        conversation.setMessage("You Created group '" + chatGroupName + "'");
        conversation.setChannelType(Message_Status.GROUP_CHANNEL_TYPE);
        conversation.setTime_stamp(chatGroup.getCreatedOnLong());
        conversation.setFrom_name(chatGroup.getName());
        conversation.setFrom_id(chatGroup.getGroupId());
        conversation.setThumb_image(ImgUrl);
        conversation.setFrom_name(chatGroup.getName());
        conversation.setUser_id(group_Id);
        return conversation;
    }
    private Chat createUserGroupForFirebase(String chatGroupName ,String group_Id, String Owner_Name) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Chat chatGroup = new Chat();
        chatGroup.setUsername(chatGroupName);
        chatGroup.setType("group_chat");
        chatGroup.setMessage(Owner_Name + " Created group '" + chatGroupName + "'");
        chatGroup.setUser_id(group_Id);
        chatGroup.setOwnder_id(currentUserId);
        chatGroup.setTimestamp(timestamp.getTime());
        return chatGroup;
    }
    private Chat createOwnerGroupForFirebase(String chatGroupName ,String group_Id) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Chat chatGroup = new Chat();
        chatGroup.setUsername(chatGroupName);
        chatGroup.setType("group_chat");
        chatGroup.setMessage("You Created group '" + chatGroupName + "'");
        chatGroup.setUser_id(group_Id);
        chatGroup.setOwnder_id(currentUserId);
        chatGroup.setTimestamp(timestamp.getTime());
        return chatGroup;
    }
    public ChatGroup decodeGroupFromSnapshot(DataSnapshot dataSnapshot) {


        ChatGroup chatGroup = new ChatGroup();
        chatGroup.setGroupId(dataSnapshot.getKey());

        if (dataSnapshot.getValue() != null) {
            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

            try {
                String iconURL = (String) map.get("iconURL");
                chatGroup.setIconURL(iconURL);
            } catch (Exception e) {
                Log.w(DEBUG_GROUPS,  "GroupsSyncronizer.decodeGroupFromSnapshot: cannot retrieve iconURL");
            }

            String owner = (String) map.get("owner");
            chatGroup.setOwner(owner);

            long createdOn = (long) map.get("createdOn");
            chatGroup.setTimestamp(createdOn);

            String name = (String) map.get("name");
            chatGroup.setName(name);

            Map<String, String> members = (Map<String, String>) map.get("members");
            chatGroup.addMembers(members);
        }

        Log.d(DEBUG_GROUPS, "GroupsSyncronizer.decodeGroupFromSnapshot: " +
                "chatGroup == " + chatGroup.toString());
        return chatGroup;
    }
}
