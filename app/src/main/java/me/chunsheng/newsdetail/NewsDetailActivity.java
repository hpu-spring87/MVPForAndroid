package me.chunsheng.newsdetail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.chunsheng.modles.NewsItem;
import me.chunsheng.mvp.R;

public class NewsDetailActivity extends AppCompatActivity implements NewsDetailView {


    @Bind(R.id.nestedScrollview)
    android.support.v4.widget.NestedScrollView nestedScrollview;
    @Bind(R.id.toolbar_layout)
    android.support.design.widget.CollapsingToolbarLayout toolbar_layout;
    @Bind(R.id.webview)
    WebView webview;

    ProgressDialog progressDialog;
    NewsItem newsItem;
    NewsDetailPresenterImpl newsDetailPresenter;

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

        newsDetailPresenter = new NewsDetailPresenterImpl(this);
        newsDetailPresenter.showProgress();

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
            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }

            });

            webview.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress > 70)
                        newsDetailPresenter.hideProgress();
                }
            });
            WebSettings settings = webview.getSettings();
            settings.setJavaScriptEnabled(true);
            newsDetailPresenter.loadNews();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this.getApplicationContext());
            progressDialog.setMessage("拼命加载新闻哇...");
        } else {
            progressDialog.show();
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    @Override
    public void loadNews() {
        if (newsItem != null)
            webview.loadUrl(newsItem.getUrl());
    }
}
