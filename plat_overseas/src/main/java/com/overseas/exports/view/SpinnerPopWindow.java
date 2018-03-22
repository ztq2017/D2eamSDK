package com.overseas.exports.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.overseas.exports.SdkManager;
import com.overseas.exports.common.util.UtilResources;

import java.util.List;

/**
 * 自定义PopupWindow  主要用来显示地区ListView
 *
 * @author Ansen
 * @create time 2015-11-3
 */
public class SpinnerPopWindow extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private List<String> list;
    private MyAdapter mAdapter;

    public SpinnerPopWindow(Context context, List<String> list, OnItemClickListener clickListener) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.list = list;
        init(clickListener);
    }

    private void init(OnItemClickListener clickListener) {
        View view = inflater.inflate(UtilResources.getLayoutId("spiner_window_layout"), null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = (ListView) view.findViewById(UtilResources.getId("listview"));
        mListView.setAdapter(mAdapter = new MyAdapter());
        mListView.setOnItemClickListener(clickListener);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(UtilResources.getLayoutId("spiner_item_layout"), null);
                holder.tvName = (TextView) convertView.findViewById(UtilResources.getId("tv_name"));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(SdkManager.defaultSDK().getLanguageContent(500 + position));
            return convertView;
        }
    }

    private class ViewHolder {
        private TextView tvName;
    }
}
