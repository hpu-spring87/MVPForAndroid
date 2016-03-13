package me.chunsheng.colorview;

/**
 * Created by weichunsheng on 16/3/6.
 */

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class CustomerRelativeLayout extends RelativeLayout {

    private static final int RED = 0xff795548;

    private static final int BLUE = 0xffE91E63;

    public CustomerRelativeLayout(Context context) {
        super(context);
        setBg();
    }


    public CustomerRelativeLayout(Context context, AttributeSet attrs,
                                  int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBg();
    }


    public CustomerRelativeLayout(Context context, AttributeSet attrs,
                                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBg();
    }


    public CustomerRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBg();
    }


    public void setBg() {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor",
                RED, BLUE);
        colorAnim.setDuration(3000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }
}