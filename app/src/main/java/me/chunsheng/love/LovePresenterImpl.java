package me.chunsheng.love;

import java.util.List;

import me.chunsheng.modles.NewsItem;

/**
 * Created by weichunsheng on 16/3/3.
 */
public class LovePresenterImpl implements LovePresenter {

    private LoveView loveView;

    public LovePresenterImpl(LoveView loveView) {
        this.loveView = loveView;
    }

    @Override
    public void showProgressDialog() {
        loveView.showPoregress();
    }

    @Override
    public void hideProgressDialog() {
        loveView.hideProgress();
    }

    @Override
    public void setData(List<NewsItem> newsItemList) {
        loveView.setData(newsItemList);
    }
}
