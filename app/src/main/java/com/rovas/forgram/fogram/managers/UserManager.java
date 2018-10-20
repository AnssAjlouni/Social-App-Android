package com.rovas.forgram.fogram.managers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rovas.forgram.fogram.Utils.IOUtils;
import com.rovas.forgram.fogram.Utils.StringUtils;

import java.io.IOException;

import static com.rovas.forgram.fogram.Utils.DebugConstants.DEBUG_LOGIN;
/**
 * Created by Mohamed El Sayed
 */
public class UserManager {
    private static final String TAG = UserManager.class.getName();
    private static final String _SERIALIZED_CHAT_CONFIGURATION_TENANT =
            "_SERIALIZED_CHAT_CONFIGURATION_TENANT";

    public static final String _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER =
            "_SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER";

    public static final String _DEFAULT_APP_ID_VALUE = "default";

    private static UserManager mInstance;

    private UserWiazrd loggedUser;
    private Context mContext;


    public static UserManager getInstance() {
//        Log.v(TAG, "getInstance");
        if (mInstance == null) {
            throw new RuntimeException("instance cannot be null. call start first.");
        }
        return mInstance;
    }
    public UserWiazrd getLoggedUser() {
//        Log.v(TAG, "ChatManager.getloggedUser");
        return loggedUser;
    }
    public void setLoggedUser(UserWiazrd loggedUser) {
        this.loggedUser = loggedUser;
        Log.d(TAG, "ChatManager.setloggedUser: loggedUser == " + loggedUser.toString());
        // serialize on disk
        IOUtils.saveObjectToFile(mContext, _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER, loggedUser);
    }

    public boolean isUserLogged() {
        Log.d(TAG, "ChatManager.isUserLogged");
        boolean isUserLogged = getLoggedUser() != null ? true : false;
        Log.d(TAG, "ChatManager.isUserLogged: isUserLogged == " + isUserLogged);
        return isUserLogged;
    }
    private void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }
    public static void start(Context context, UserWiazrd currentUser) {
        Log.i(TAG, "starting");

//        // multidex support
//        // source :
//        // https://forums.xamarin.com/discussion/64234/multi-dex-app-with-a-custom-application-class-that-runs-on-pre-lollipop
//        MultiDex.install(context);

        // create a new chat
        UserManager chat = new UserManager(); // create the instance of the chat

        chat.setContext(context);

        mInstance = chat;

//        chat.loggedUser = currentUser;
        // serialize the current user
//        IOUtils.saveObjectToFile(context, _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER, currentUser);
        chat.setLoggedUser(currentUser);


        // serialize the appId
        IOUtils.saveObjectToFile(context, _SERIALIZED_CHAT_CONFIGURATION_TENANT, currentUser);
    }
    private void removeLoggedUser() {
        // clear all logged user data
        IOUtils.deleteObject(mContext, _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER);
    }

}
