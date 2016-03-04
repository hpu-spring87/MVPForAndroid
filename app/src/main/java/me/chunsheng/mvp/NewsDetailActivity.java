package me.chunsheng.mvp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.chunsheng.modles.NewsItem;

public class NewsDetailActivity extends AppCompatActivity {


    @Bind(R.id.nestedScrollview)
    android.support.v4.widget.NestedScrollView nestedScrollview;
    @Bind(R.id.toolbar_layout)
    android.support.design.widget.CollapsingToolbarLayout toolbar_layout;
    @Bind(R.id.webview)
    WebView webview;

    NewsItem newsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        ButterKnife.bind(this);
        newsItem = (NewsItem) getIntent().getSerializableExtra("NewItem");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(android.R.drawable.ic_menu_close_clear_cancel);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (newsItem != null) {
            toolbar_layout.setCollapsedTitleTextAppearance(R.style.NewsDetailTitleSmall);
            toolbar_layout.setExpandedTitleTextAppearance(R.style.NewsDetailTitleBig);
            toolbar_layout.setTitle(newsItem.getTitle());
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Drawable drawable = new BitmapDrawable(bitmap);
                    toolbar_layout.setBackgroundDrawable(drawable);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }

            };
            if (newsItem.getFirstImg().length() > 5)
                Picasso.with(this).load(newsItem.getFirstImg()).into(target);

            webview.loadUrl(newsItem.getUrl());
            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });
            WebSettings settings = webview.getSettings();
            settings.setJavaScriptEnabled(true);

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, newsItem.getTitle() + newsItem.getUrl());
                shareIntent.setType("text/plain");

                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, "分享到"));
            }
        });

    }


}
