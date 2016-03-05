package me.chunsheng.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import me.chunsheng.modles.NewsItem;
import me.chunsheng.mvp.R;

/**
 * Created by tarek on 11/7/15.
 */

public class NewsRecyclerViewAdapter
        extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private List<NewsItem> photoList;
    private final LayoutInflater mLayoutInflater;

    public NewsRecyclerViewAdapter(Context context, List<NewsItem> photoList) {
        this.photoList = photoList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false));
        //return new ViewHolder(mLayoutInflater.inflate(R.layout.item_news, parent, false));
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.news_title.setText(photoList.get(position).getTitle());
        holder.news_from.setText(photoList.get(position).getSource());
        Random r = new Random();
        holder.news_time.setText(r.nextInt(10) + 1 + "分钟");
        holder.news_love.setText(r.nextInt(1000) + 1000 + "赞");
        if (photoList.get(position).getFirstImg() != null && photoList.get(position).getFirstImg().length() > 5) {
            Picasso.with(holder.imageView.getContext())
                    .load(photoList.get(position).getFirstImg().replace("\\", "")).resize(500, 300).into(holder.imageView);
        }
    }


    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView news_title;
        private TextView news_from;
        private TextView news_time;
        private TextView news_love;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.news_pic);
            news_title = (TextView) view.findViewById(R.id.news_title);
            news_from = (TextView) view.findViewById(R.id.news_from);
            news_time = (TextView) view.findViewById(R.id.news_time);
            news_love = (TextView) view.findViewById(R.id.news_love);
        }

    }

}
