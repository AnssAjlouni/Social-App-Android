package com.rovas.forgram.fogram.Utils.SpinKit;


import com.rovas.forgram.fogram.Utils.SpinKit.sprite.Sprite;
import com.rovas.forgram.fogram.Utils.SpinKit.style.ChasingDots;
import com.rovas.forgram.fogram.Utils.SpinKit.style.Circle;
import com.rovas.forgram.fogram.Utils.SpinKit.style.CubeGrid;
import com.rovas.forgram.fogram.Utils.SpinKit.style.DoubleBounce;
import com.rovas.forgram.fogram.Utils.SpinKit.style.FadingCircle;
import com.rovas.forgram.fogram.Utils.SpinKit.style.FoldingCube;
import com.rovas.forgram.fogram.Utils.SpinKit.style.MultiplePulse;
import com.rovas.forgram.fogram.Utils.SpinKit.style.MultiplePulseRing;
import com.rovas.forgram.fogram.Utils.SpinKit.style.Pulse;
import com.rovas.forgram.fogram.Utils.SpinKit.style.PulseRing;
import com.rovas.forgram.fogram.Utils.SpinKit.style.RotatingCircle;
import com.rovas.forgram.fogram.Utils.SpinKit.style.RotatingPlane;
import com.rovas.forgram.fogram.Utils.SpinKit.style.ThreeBounce;
import com.rovas.forgram.fogram.Utils.SpinKit.style.WanderingCubes;
import com.rovas.forgram.fogram.Utils.SpinKit.style.Wave;

/**
 * Created by Mohamed El Sayed.
 */
public class SpriteFactory {

    public static Sprite create(Style style) {
        Sprite sprite = null;
        switch (style) {
            case ROTATING_PLANE:
                sprite = new RotatingPlane();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case ROTATING_CIRCLE:
                sprite = new RotatingCircle();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}
