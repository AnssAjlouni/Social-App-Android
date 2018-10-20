package com.rovas.forgram.fogram.interfaces;


import android.view.View;

import com.rovas.forgram.fogram.models.Img;

/**
 * Created by Mohamed El Sayed
 */
public interface OnSelectionListener {
    void OnClick(Img Img, View view, int position);

    void OnLongClick(Img img, View view, int position);
}