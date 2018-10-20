package com.rovas.forgram.fogram.Utils.SpinKit.style;

import android.animation.ValueAnimator;


import com.rovas.forgram.fogram.Utils.SpinKit.animation.SpriteAnimatorBuilder;
import com.rovas.forgram.fogram.Utils.SpinKit.sprite.CircleSprite;

/**
 * Created by Mohamed El Sayed.
 */
public class Pulse extends CircleSprite {

    public Pulse() {
        setScale(0f);
    }

    @Override
    public ValueAnimator onCreateAnimation() {
        float fractions[] = new float[]{0f, 1f};
        return new SpriteAnimatorBuilder(this).
                scale(fractions, 0f, 1f).
                alpha(fractions, 255, 0).
                duration(1000).
                easeInOut(fractions)
                .build();
    }
}
