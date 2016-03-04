package me.chunsheng.love;

import java.util.List;

import me.chunsheng.modles.NewsItem;

/**
 * Created by weichunsheng on 16/3/3.
 */
public interface LoveView {

    void setData(List<NewsItem> list);

    void showPoregress();

    void hideProgress();
}
