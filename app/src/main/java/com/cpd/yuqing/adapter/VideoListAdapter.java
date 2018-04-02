package com.cpd.yuqing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cpd.yuqing.R;
import com.cpd.yuqing.db.vo.video.News;
import com.cpd.yuqing.util.GlideUtils;

import java.util.ArrayList;

/**
 * Created by s21v on 2018/3/29.
 */

public class VideoListAdapter extends BaseAdapter {
    private ArrayList<News> data;
    private Context context;

    public VideoListAdapter(Context context, ArrayList<News> data) {
        this.data = data;
        this.context = context;
    }

    public ArrayList<News> getData() {
        return data;
    }

    public void setData(ArrayList<News> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.video_list_item,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTv = convertView.findViewById(R.id.titleTv);
            viewHolder.sourceTv = convertView.findViewById(R.id.sourceTv);
            viewHolder.pubTimeTv = convertView.findViewById(R.id.pubTimeTv);
            viewHolder.thumbIconIv = convertView.findViewById(R.id.thumbIconIv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        News news = data.get(position);
        viewHolder.titleTv.setText(news.getTitle());
        viewHolder.sourceTv.setText(news.getSource());
        viewHolder.pubTimeTv.setText(news.minPubTime());
        Glide.with(context).load(news.getThumbIconUrl()).into(viewHolder.thumbIconIv);
        return convertView;
    }

    class ViewHolder {
        TextView titleTv;
        TextView sourceTv;
        TextView pubTimeTv;
        ImageView thumbIconIv;
    }
}
