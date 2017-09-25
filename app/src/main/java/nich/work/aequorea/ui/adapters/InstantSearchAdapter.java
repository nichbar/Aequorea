package nich.work.aequorea.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nich.work.aequorea.R;

public class InstantSearchAdapter extends BaseAdapter {
    
    private ArrayList<InstantSearchBean> mData;
    private LayoutInflater mInflater;
    private int mActionBarSize;
    
    public InstantSearchAdapter(Context context, ArrayList<InstantSearchBean> instantSearchBeen) {
        mInflater = LayoutInflater.from(context);
        mData = instantSearchBeen;
        mActionBarSize = (int) context.getResources().getDimension(R.dimen.search_item_height);
    }
    
    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }
    
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    
        InstantSearchAdapter.SuggestionsViewHolder viewHolder;
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search, parent, false);
            viewHolder = new InstantSearchAdapter.SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InstantSearchAdapter.SuggestionsViewHolder) convertView.getTag();
        }
        
        String currentListData = ((InstantSearchBean) getItem(position)).title;
        
        viewHolder.textView.setText(currentListData);
        convertView.setTag(R.string.app_name, mData.get(position));
        
        return convertView;
    }
    
    public int getListHeight() {
        return mActionBarSize * getCount();
    }
    
    private class SuggestionsViewHolder {
        
        TextView textView;
        ImageView imageView;
        
        private SuggestionsViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.search_text);
        }
    }
    
    public static class InstantSearchBean {
        public long id;
        public String title;
    
        public InstantSearchBean(long id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
