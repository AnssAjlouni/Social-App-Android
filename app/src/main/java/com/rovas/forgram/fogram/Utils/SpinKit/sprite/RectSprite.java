package com.rovas.forgram.fogram.Utils.SpinKit.sprite;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Mohamed El Sayed.
 */
public class RectSprite extends ShapeSprite {
    @Override
    public ValueAnimator onCreateAnimation() {
        return null;
    }

    @Override
    public void drawShape(Canvas canvas, Paint paint) {
        if (getDrawBounds() != null) {
            canvas.drawRect(getDrawBounds(), paint);
        }
    }
}
