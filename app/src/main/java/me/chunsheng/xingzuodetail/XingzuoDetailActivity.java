package me.chunsheng.xingzuodetail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.net.URLEncoder;

import me.chunsheng.modles.XingZuoItem;
import me.chunsheng.modles.XingZuoWeekItem;
import me.chunsheng.mvp.R;
import me.chunsheng.utils.OKHttpUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class XingzuoDetailActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private TextView mContentView;
    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5;
    private TextView textView4, textView3, textView2, textView1, textView_love, textView_health, textView_money, textView_work;
    private OKHttpUtils okHttpUtils;
    //白羊座，金牛座，双子座，巨蟹座，狮子座，处女座，天秤座，天蝎座，射手座，摩羯座，水瓶座，双鱼座
    private String[] items = {"白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("星座运势");
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_xingzuo_detail);

        mVisible = true;
        okHttpUtils = new OKHttpUtils.Builder(this).build();
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (TextView) findViewById(R.id.fullscreen_content);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar5 = (ProgressBar) findViewById(R.id.progressBar5);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView_health = (TextView) findViewById(R.id.textView_health);
        textView_love = (TextView) findViewById(R.id.textView_love);
        textView_money = (TextView) findViewById(R.id.textView_money);
        textView_work = (TextView) findViewById(R.id.textView_work);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.dummy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        XingzuoDetailActivity.this);
                builder.setTitle("请选择星座:");
                builder.setIcon(R.mipmap.love);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String select_item = items[which].toString();
                        getDayData(select_item);
                        getWeekData(select_item);
                    }
                });
                builder.show();
            }
        });

        getDayData("白羊座");
        getWeekData("白羊座");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //获取数据
    public void getDayData(final String xingzuo) {
        try {
            okHttpUtils.get("http://web.juhe.cn:8080/constellation/getAll?consName=" + URLEncoder.encode(xingzuo, "UTF-8") + "&type=today&key=28dbf0c1c1e5abedacddf2ea46fd1b89", new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String result = response.body().string();
                        final XingZuoItem reasonDataBak = JSON.parseObject(result, XingZuoItem.class);
                        if (reasonDataBak != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mContentView.setText(reasonDataBak.getName());
                                    textView1.setText(reasonDataBak.getName() + ":    " + reasonDataBak.getDatetime());
                                    textView2.setText(reasonDataBak.getSummary());
                                    textView3.setText(reasonDataBak.getNumber());
                                    textView4.setText(reasonDataBak.getQFriend());
                                    progressBar1.setProgress(Integer.parseInt(reasonDataBak.getAll().replace("%", "")));
                                    progressBar2.setProgress(Integer.parseInt(reasonDataBak.getHealth().replace("%", "")));
                                    progressBar3.setProgress(Integer.parseInt(reasonDataBak.getLove().replace("%", "")));
                                    progressBar4.setProgress(Integer.parseInt(reasonDataBak.getWork().replace("%", "")));
                                    progressBar5.setProgress(Integer.parseInt(reasonDataBak.getMoney().replace("%", "")));
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //获取数据
    public void getWeekData(final String xingzuo) {
        try {
            okHttpUtils.get("http://web.juhe.cn:8080/constellation/getAll?consName=" + URLEncoder.encode(xingzuo, "UTF-8") + "&type=week&key=28dbf0c1c1e5abedacddf2ea46fd1b89", new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String result = response.body().string();
                        final XingZuoWeekItem reasonDataBak = JSON.parseObject(result, XingZuoWeekItem.class);
                        if (reasonDataBak != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView_love.setText(reasonDataBak.getLove());
                                    textView_money.setText(reasonDataBak.getMoney());
                                    textView_health.setText(reasonDataBak.getHealth());
                                    textView_work.setText(reasonDataBak.getWork());
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
