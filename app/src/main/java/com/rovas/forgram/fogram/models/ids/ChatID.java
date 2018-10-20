package com.rovas.forgram.fogram.models.ids;


import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
/**
 * Created by Mohamed El Sayed
 */
public class ChatID {
    @Exclude
    public String ChatID;
    public  <T extends ChatID> T withid (@NonNull final String ID)
    {
        this.ChatID = ID;
        return (T)this;
    }

}
