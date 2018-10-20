package com.rovas.forgram.fogram.managers;

import com.rovas.forgram.fogram.models.ChatGroup;

/**
 * Created by Mohamed El Sayed
 */

public class WizardNewGroup {
//    private static final String PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP =
//            "PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP";

    private ChatGroup tempChatGroup = new ChatGroup();

    // singleton
    // source : https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0
    private static volatile WizardNewGroup instance;

    //private constructor.
    private WizardNewGroup() {

        // Prevent form the reflection api.
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static WizardNewGroup getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            synchronized (WizardNewGroup.class) {
                if (instance == null) instance = new WizardNewGroup();
            }
        }

        return instance;
    }

//    // Make singleton from serialize and deserialize operation.
//    protected WizardNewGroup readResolve() {
//        return getInstance();
//    }
    // end singleton

    public ChatGroup getTempChatGroup() {
        return tempChatGroup;
    }

    public void dispose() {
        clearTempGroup();
    }

    private void clearTempGroup() {
        tempChatGroup.setGroupId("");
        //TODO
        //tempChatGroup.getCreatedOn();
        tempChatGroup.setTimestamp(0L);
        tempChatGroup.setIconURL("");
        tempChatGroup.getMembers().clear();
        tempChatGroup.getMembersList().clear();
        tempChatGroup.setName("");
        tempChatGroup.setOwner("");
    }
}
