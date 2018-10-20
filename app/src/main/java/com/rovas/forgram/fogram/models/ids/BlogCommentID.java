package com.rovas.forgram.fogram.models.ids;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
/**
 * Created by Mohamed El Sayed
 */
public class BlogCommentID {
    @Exclude
    public String BlogCommentID;
    public  <T extends BlogCommentID> T withid (@NonNull final String ID)
    {
        this.BlogCommentID = ID;
        return (T)this;
    }

}
