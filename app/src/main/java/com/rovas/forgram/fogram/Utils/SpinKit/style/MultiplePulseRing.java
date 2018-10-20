package com.rovas.forgram.fogram.Utils.SpinKit.style;


import com.rovas.forgram.fogram.Utils.SpinKit.sprite.Sprite;
import com.rovas.forgram.fogram.Utils.SpinKit.sprite.SpriteContainer;

/**
 * Created by Mohamed El Sayed.
 */
public class MultiplePulseRing extends SpriteContainer {

    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new PulseRing(),
                new PulseRing(),
                new PulseRing(),
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnimationDelay(200 * (i + 1));
        }
    }
}
