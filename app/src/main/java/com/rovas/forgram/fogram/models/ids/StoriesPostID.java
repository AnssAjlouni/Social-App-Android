package com.rovas.forgram.fogram.models.ids;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
/**
 * Created by Mohamed El Sayed
 */
public class StoriesPostID {

    @Exclude
    public String StoriesPostID;
    public <T extends StoriesPostID> T withid (@NonNull final String id)
    {
        this.StoriesPostID = id;
        return (T) this;
    }
}
