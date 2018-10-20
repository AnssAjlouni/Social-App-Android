package com.rovas.forgram.fogram.Utils.SpinKit.animation.interpolator;

import android.view.animation.Interpolator;

/**
 * Created by Mohamed El Sayed.
 */
public class Ease {
    public static Interpolator inOut() {
        return PathInterpolatorCompat.create(0.42f, 0f, 0.58f, 1f);
    }
}
