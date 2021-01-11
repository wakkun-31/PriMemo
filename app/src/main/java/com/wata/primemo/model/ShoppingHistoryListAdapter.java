package com.wata.primemo.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wata.primemo.R;

import java.util.ArrayList;

public class ShoppingHistoryListAdapter extends BaseAdapter {

    private ArrayList<DateItem> shoppingDateList;
    private Context mContext;
    private int mResource;

    public ShoppingHistoryListAdapter(Context context, ArrayList<DateItem> dateList, int resource) {
        this.mContext = context;
        this.shoppingDateList = dateList;
        this.mResource = resource;
    }

    @Override
    public int getCount() {
        return shoppingDateList.size();
    }

    @Override
    public Object getItem(int position) {
        return shoppingDateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return shoppingDateList.get(position).getDateId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) mContext;
        DateItem item = (DateItem) getItem(position);
        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(mResource, null);
        }
        // Todo (今後)アイコンや場所項目追加で変更要
//        ((ImageView) convertView.findViewById(R.id.iv_date_icon)).setImageResource(item.getDateIconResource());
        ((TextView) convertView.findViewById(R.id.tv_date)).setText(item.getDate());
//        ((TextView) convertView.findViewById(R.id.tv_location)).setText(item.getLocation());

        return convertView;
    }
}
