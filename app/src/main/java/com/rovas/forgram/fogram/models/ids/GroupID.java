package com.rovas.forgram.fogram.models.ids;


import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
/**
 * Created by Mohamed El Sayed
 */
public class GroupID {
    @Exclude
    public String GroupID;
    public  <T extends GroupID> T withid (@NonNull final String ID)
    {
        this.GroupID = ID;
        return (T)this;
    }

}
