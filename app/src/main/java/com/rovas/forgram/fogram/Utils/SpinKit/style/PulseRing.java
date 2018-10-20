package com.rovas.forgram.fogram.Utils.SpinKit.style;

import android.animation.ValueAnimator;

import com.rovas.forgram.fogram.Utils.SpinKit.animation.SpriteAnimatorBuilder;
import com.rovas.forgram.fogram.Utils.SpinKit.animation.interpolator.KeyFrameInterpolator;
import com.rovas.forgram.fogram.Utils.SpinKit.sprite.RingSprite;

/**
 * Created by Mohamed El Sayed.
 */
public class PulseRing extends RingSprite {

    public PulseRing() {
        setScale(0f);
    }

    @Override
    public ValueAnimator onCreateAnimation() {
        float fractions[] = new float[]{0f, 0.7f, 1f};
        return new SpriteAnimatorBuilder(this).
                scale(fractions, 0f, 1f, 1f).
                alpha(fractions, 255, (int) (255 * 0.7), 0).
                duration(1000).
                interpolator(KeyFrameInterpolator.pathInterpolator(0.21f, 0.53f, 0.56f, 0.8f, fractions)).
                build();
    }
}
