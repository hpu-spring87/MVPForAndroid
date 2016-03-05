package me.chunsheng.love;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.chunsheng.adapters.NewsRecyclerViewAdapter;
import me.chunsheng.adapters.RecyclerItemClickListener;
import me.chunsheng.modles.NewsItem;
import me.chunsheng.newsdetail.NewsDetailActivity;
import me.chunsheng.mvp.R;
import me.chunsheng.utils.OKHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoveFragment extends Fragment implements LoveView {


    private LovePresenterImpl lovePresenter;
    @Bind(R.id.newsRecyclerView)
    android.support.v7.widget.RecyclerView newsRecyclerView;
    private OKHttpUtils okHttpUtils;
    ProgressDialog progressBar;

    public LoveFragment() {
        // Required empty public constructor
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LoveFragment newInstance(int sectionNumber) {
        LoveFragment fragment = new LoveFragment();
        Bundle args = new Bundle();
        args.putInt("params", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_love, container, false);

        ButterKnife.bind(this, rootView);

        lovePresenter = new LovePresenterImpl(this);
        okHttpUtils = new OKHttpUtils.Builder(getActivity()).build();
        lovePresenter.showProgressDialog();
        getData();
        return rootView;
    }


    @Override
    public void setData(final List<NewsItem> list) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                newsRecyclerView.setLayoutManager(llm);
                NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(getActivity(), list);
                newsRecyclerView.setAdapter(newsRecyclerViewAdapter);
                newsRecyclerView.setHasFixedSize(true);
                newsRecyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent it = new Intent(getActivity(), NewsDetailActivity.class);
                                it.putExtra("NewItem", list.get(position));
                                getContext().startActivity(it);
                            }
                        }));
            }
        });
    }

    @Override
    public void showPoregress() {
        if (progressBar != null) {
            //progressBar.show();
        } else {
            progressBar = new ProgressDialog(getActivity().getApplicationContext());
            progressBar.setMessage("       Loading...٩(͡๏̯͡๏)۶");
            try {
                //if (this.isVisible() && getActivity().isFinishing())
                    //progressBar.show();

            } catch (Exception e) {
            }
        }
    }

    @Override
    public void hideProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.hide();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressBar != null) {
            progressBar.dismiss();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //if (!isVisibleToUser)
            //progressBar.dismiss();
    }

    //获取数据
    public List<NewsItem> getData() {
        okHttpUtils.get("http://v.juhe.cn/weixin/query?key=255a05323dc642981365ea70691d3a68", new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String result = response.body().string();
                    String reasonData = new JSONObject(result).getString("reason");
                    if (reasonData != null && "success".equals(reasonData)) {
                        String resultData = new JSONObject(result).getString("result");
                        String listData = new JSONObject(resultData).getString("list");
                        final List<NewsItem> list = JSON.parseArray(listData, NewsItem.class);
                        lovePresenter.setData(list);
                        lovePresenter.hideProgressDialog();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return null;
    }
}
