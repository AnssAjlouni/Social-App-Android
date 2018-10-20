package com.rovas.forgram.fogram.managers;

import android.content.Context;

import com.rovas.forgram.fogram.models.ChatGroup;
import com.rovas.forgram.fogram.models.User;

import java.io.Serializable;

/**
 * Created by Mohamed El Sayed
 */
public class UserWiazrd implements Serializable {
//    private static final String PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP =
//            "PRIVATE_NEW_GROUP_WIZARD_SERIALIZABLE_CHAT_GROUP";

    private User tempUser = new User();

    // singleton
    // source : https://android.jlelse.eu/how-to-make-the-perfect-singleton-de6b951dfdb0
    private static volatile UserWiazrd instance = new UserWiazrd();
    private Context mContext;
    //private constructor.
    private UserWiazrd() {

        //set the default mContext value equals to ChatManager.getInstance().getContext() Use ChatUI.getIntance().setContext to use another context
        //mContext = UserManager.getInstance().getContext();

        // Prevent form the reflection api.
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static UserWiazrd getInstance() {
        if (instance == null) { //if there is no instance available... create new one
            synchronized (UserWiazrd.class) {
                if (instance == null) instance = new UserWiazrd();
            }
        }

        return instance;
    }

//    // Make singleton from serialize and deserialize operation.
//    protected WizardNewGroup readResolve() {
//        return getInstance();
//    }
    // end singleton

    public User getTempUser() {
        return tempUser;
    }

    public void dispose() {
        clearTempUser();
    }

    private void clearTempUser() {
        tempUser.setName("");
        tempUser.setUser_id("");
        tempUser.setUsername("");
        tempUser.setStatus("");
        tempUser.setThumb_image("");
        tempUser.setBirth_date(0);
        tempUser.setCreated_date(0);
        tempUser.setRole(0);
        tempUser.setDevice_token("");
        tempUser.setToken_id("");
        tempUser.setFollowers(0);
        tempUser.setFollowing(0);
        tempUser.setPhone_number(0);
        tempUser.setNotificationList(null);
        tempUser.setChatList(null);
    }
}
