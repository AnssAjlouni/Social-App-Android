package com.rovas.forgram.fogram.Utils.Views;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.rovas.forgram.fogram.R;
import com.rovas.forgram.fogram.enums.FollowState;

/**
 * Created by Mohamed El Sayed
 */
public class FollowButton extends android.support.v7.widget.AppCompatButton {
    public static final String TAG = FollowButton.class.getSimpleName();

    public static final int FOLLOW_STATE = 1;
    public static final int FOLLOW_BACK_STATE = 2;
    public static final int FOLLOWING_STATE = 3;
    public static final int INVISIBLE_STATE = -1;

    private int state;


    public FollowButton(Context context) {
        super(context);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FollowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setState(FollowState followState) {
        switch (followState) {
            case I_FOLLOW_USER:
            case FOLLOW_EACH_OTHER:
                state = FOLLOWING_STATE;
                break;
            case USER_FOLLOW_ME:
                state = FOLLOW_BACK_STATE;
                break;
            case NO_ONE_FOLLOW:
                state = FOLLOW_STATE;
                break;
            case MY_PROFILE:
                state = INVISIBLE_STATE;

        }

        updateButtonState();
    }

    private void init() {
        state = INVISIBLE_STATE;
        updateButtonState();
    }

    public int getState() {
        return state;
    }

    public void updateButtonState() {
        setClickable(true);

        switch (state) {
            case FOLLOW_STATE: {
                setVisibility(VISIBLE);
                setText(R.string.button_follow_title);
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.follow_button_dark_bg));
                setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                break;
            }

            case FOLLOW_BACK_STATE: {
                setVisibility(VISIBLE);
                setText(R.string.button_follow_back_title);
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.follow_button_dark_bg));
                setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                break;
            }

            case FOLLOWING_STATE: {
                setVisibility(VISIBLE);
                setText(R.string.button_following);
                setBackground(ContextCompat.getDrawable(getContext(), R.drawable.follow_button_light_bg));
                setTextColor(ContextCompat.getColor(getContext(), R.color.primary_dark_text));
                break;
            }

            case INVISIBLE_STATE: {
                setVisibility(INVISIBLE);
                setClickable(false);
                break;
            }
        }
    }
}
