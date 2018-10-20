package com.rovas.forgram.fogram.Utils.SpinKit.style;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Build;

import com.rovas.forgram.fogram.Utils.SpinKit.animation.SpriteAnimatorBuilder;
import com.rovas.forgram.fogram.Utils.SpinKit.sprite.RectSprite;
import com.rovas.forgram.fogram.Utils.SpinKit.sprite.Sprite;
import com.rovas.forgram.fogram.Utils.SpinKit.sprite.SpriteContainer;

/**
 * Created by Mohamed El Sayed.
 */
public class Wave extends SpriteContainer {

    @Override
    public Sprite[] onCreateChild() {
        WaveItem[] waveItems = new WaveItem[5];
        for (int i = 0; i < waveItems.length; i++) {
            waveItems[i] = new WaveItem();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                waveItems[i].setAnimationDelay(600 + i * 100);
            } else {
                waveItems[i].setAnimationDelay(-1200 + i * 100);
            }

        }
        return waveItems;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        bounds = clipSquare(bounds);
        int rw = bounds.width() / getChildCount();
        int width = bounds.width() / 5 * 3 / 5;
        for (int i = 0; i < getChildCount(); i++) {
            Sprite sprite = getChildAt(i);
            int l = bounds.left + i * rw + rw / 5;
            int r = l + width;
            sprite.setDrawBounds(l, bounds.top, r, bounds.bottom);
        }
    }

    private class WaveItem extends RectSprite {

        WaveItem() {
            setScaleY(0.4f);
        }

        @Override
        public ValueAnimator onCreateAnimation() {
            float fractions[] = new float[]{0f, 0.2f, 0.4f, 1f};
            return new SpriteAnimatorBuilder(this).scaleY(fractions, 0.4f, 1f, 0.4f, 0.4f).
                    duration(1200).
                    easeInOut(fractions)
                    .build();
        }
    }
}
