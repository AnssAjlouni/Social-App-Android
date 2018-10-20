package com.rovas.forgram.fogram.interfaces;

import com.rovas.forgram.fogram.models.Following;

import java.io.Serializable;

/**
 * Created by Mohamed El Sayed
 */
public interface OnContactClickListener extends Serializable {
    void onContactClicked(Following contact, int position);
}
