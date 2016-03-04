package me.chunsheng.love;

import java.util.ArrayList;
import java.util.List;

import me.chunsheng.modles.NewsItem;

/**
 * Created by weichunsheng on 16/3/3.
 */
public interface LovePresenter {

    void showProgressDialog();

    void hideProgressDialog();

    void setData(List<NewsItem> newsItemList);
}
