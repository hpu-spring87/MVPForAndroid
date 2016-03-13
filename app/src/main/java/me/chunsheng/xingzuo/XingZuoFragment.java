package me.chunsheng.xingzuo;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import java.util.ArrayList;

import me.chunsheng.colorview.ShapeHolder;
import me.chunsheng.mvp.R;
import me.chunsheng.xingzuodetail.XingzuoDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class XingZuoFragment extends Fragment {


    public XingZuoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment  container_rl
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_xing_zuo, container, false);
        me.chunsheng.colorview.CustomerRelativeLayout customerRelativeLayout = (me.chunsheng.colorview.CustomerRelativeLayout) rootView.findViewById(R.id.container_rl);
        customerRelativeLayout.addView(new MyAnimationView(getActivity()), 0);

        me.chunsheng.colorview.CircleImageView circleImageView = (me.chunsheng.colorview.CircleImageView) rootView.findViewById(R.id.iv_btn);
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(20000);//设置动画持续时间
        /** 常用方法 */
        animation.setRepeatCount(10);//设置重复次数
        animation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
        animation.setStartOffset(0);//执行前的等待时间
        animation.setInterpolator(new LinearInterpolator());//不停顿
        circleImageView.setAnimation(animation);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), XingzuoDetailActivity.class));
            }
        });

        return rootView;
    }


    public class MyAnimationView extends View {
        private static final int RED = 0xffFF8080;
        private static final int BLUE = 0xff8080FF;
        private static final int CYAN = 0xff80ffff;
        private static final int GREEN = 0xff80ff80;
        public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
        AnimatorSet animation = null;

        public MyAnimationView(Context context) {
            super(context);
            /**************************************************************************************************
                          *
                          * 设置自定义view的背景，是一个渐变的过程，用ValueAnimator实现，ValueAnimator是Property
                          * Animation系统 的核心类，它包含了配置Property Animation属性的大部分方法，那要实现一个Property
                          * Animation,都需要直接 或间接使用ValueAnimator类，使用ValueAnimator的步骤如下：
                          * 1.调用ValueAnimation类中的ofInt(int...values)、ofFloat(String
                          * propertyName,float...values)等静态方
                          * 法实例化ValueAnimator对象，并设置目标属性的属性名、初始值或结束值等值;
                          * 2.调用addUpdateListener(AnimatorUpdateListener
                          * mListener)方法为ValueAnimator对象设置属性变化的监听器
                          * 3.创建自定义的Interpolator，调用setInterpolator(TimeInterpolator
                          * value)为ValueAniamtor设置自定义的 Interpolator;(可选，不设置默认为缺省值)
                          * 4.创建自定义的TypeEvaluator,调用setEvaluator(TypeEvaluator
                          * value)为ValueAnimator设置自定义的 TypeEvaluator;(可选，不设置默认为缺省值)
                          * 5.在AnimatorUpdateListener 中的实现方法为目标对象的属性设置计算好的属性值。
                          * 6.设置动画的持续时间、是否重复及重复次数等属性; 7.为ValueAnimator设置目标对象并开始执行动画。
                          *
                          * *****************************************************************
                          */
            ValueAnimator colorAnim = ObjectAnimator.ofInt(this,
                    "backgroundColor", RED, BLUE);
            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();
        }

        @SuppressLint("NewApi")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN
                    && event.getAction() != MotionEvent.ACTION_MOVE) {
                return false;
            }
            ShapeHolder newBall = addBall(event.getX(), event.getY());
            /** Bouncing animation with squash and stretch **/
            float startY = newBall.getY();
            float endY = getHeight() - 50f;
            float h = (float) getHeight();
            float eventY = event.getY();
            int duration = (int) (500 * ((h - eventY) / h));
            ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y",
                    startY, endY);
            bounceAnim.setDuration(duration);
            bounceAnim.setInterpolator(new AccelerateInterpolator());
            ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x",
                    newBall.getX(), newBall.getX() - 25f);
            squashAnim1.setDuration(duration / 4);
            squashAnim1.setRepeatCount(1);
            squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim1.setInterpolator(new DecelerateInterpolator());
            ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall,
                    "width", newBall.getWidth(), newBall.getWidth() + 50);
            squashAnim2.setDuration(duration / 4);
            squashAnim2.setRepeatCount(1);
            squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim2.setInterpolator(new DecelerateInterpolator());
            ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y",
                    endY, endY + 25f);
            stretchAnim1.setDuration(duration / 4);
            stretchAnim1.setRepeatCount(1);
            stretchAnim1.setInterpolator(new DecelerateInterpolator());
            stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall,
                    "height", newBall.getHeight(), newBall.getHeight() - 25);
            stretchAnim2.setDuration(duration / 4);
            stretchAnim2.setRepeatCount(1);
            stretchAnim2.setInterpolator(new DecelerateInterpolator());
            stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y",
                    endY, startY);
            bounceBackAnim.setDuration(duration);
            bounceBackAnim.setInterpolator(new DecelerateInterpolator());
            // Sequence the down/squash&stretch/up animations
            AnimatorSet bouncer = new AnimatorSet();
            bouncer.play(bounceAnim).before(squashAnim1);
            bouncer.play(squashAnim1).with(squashAnim2);
            bouncer.play(squashAnim1).with(stretchAnim1);
            bouncer.play(squashAnim1).with(stretchAnim2);
            bouncer.play(bounceBackAnim).after(stretchAnim2);
            // Fading animation - remove the ball when the animation is done
            ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha",
                    1f, 0f);
            fadeAnim.setDuration(250);
            fadeAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    balls.remove(((ObjectAnimator) animation).getTarget());
                }
            });

            // Sequence the two animations to play one after the other
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(bouncer).before(fadeAnim);
            // Start the animation
            animatorSet.start();
            return true;
        }

        private ShapeHolder addBall(float x, float y) {
            OvalShape circle = new OvalShape();
            circle.resize(50f, 50f);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            shapeHolder.setX(x - 25f);
            shapeHolder.setY(y - 25f);
            int red = (int) (Math.random() * 255);
            int green = (int) (Math.random() * 255);
            int blue = (int) (Math.random() * 255);
            int color = 0xff000000 | red << 16 | green << 8 | blue;
            Paint paint = drawable.getPaint(); // new
            // Paint(Paint.ANTI_ALIAS_FLAG);
            int darkColor = 0xff000000 | red / 4 << 16 | green / 4 << 8 | blue
                    / 4;
            RadialGradient gradient = new RadialGradient(37.5f, 12.5f, 50f,
                    color, darkColor, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
            shapeHolder.setPaint(paint);
            balls.add(shapeHolder);
            return shapeHolder;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < balls.size(); ++i) {
                ShapeHolder shapeHolder = balls.get(i);
                canvas.save();
                canvas.translate(shapeHolder.getX(), shapeHolder.getY());
                shapeHolder.getShape().draw(canvas);
                canvas.restore();
            }
        }
    }


}
