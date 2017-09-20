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
    
    private ArrayList<InstantSearchBean> data;
    private Drawable suggestionIcon;
    private LayoutInflater inflater;
    private boolean ellipsize;
    
    public InstantSearchAdapter(Context context, ArrayList<InstantSearchBean> instantSearchBeen) {
        inflater = LayoutInflater.from(context);
        data = instantSearchBeen;
    }
    
    public InstantSearchAdapter(Context context, ArrayList<InstantSearchBean> instantSearchBeen, Drawable suggestionIcon, boolean ellipsize) {
        inflater = LayoutInflater.from(context);
        data = instantSearchBeen;
        this.suggestionIcon = suggestionIcon;
        this.ellipsize = ellipsize;
    }
    
    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
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
    
        InstantSearchAdapter.SuggestionsViewHolder viewHolder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_search, parent, false);
            viewHolder = new InstantSearchAdapter.SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (InstantSearchAdapter.SuggestionsViewHolder) convertView.getTag();
        }
        
        String currentListData = ((InstantSearchBean) getItem(position)).title;
        
        viewHolder.textView.setText(currentListData);
        if (ellipsize) {
            viewHolder.textView.setSingleLine();
            viewHolder.textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        convertView.setTag(R.string.app_name, data.get(position));
        
        return convertView;
    }
    
    private class SuggestionsViewHolder {
        
        TextView textView;
        ImageView imageView;
        
        private SuggestionsViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(R.id.search_text);
            if (suggestionIcon != null) {
                imageView = (ImageView) convertView.findViewById(R.id.search_icon);
                imageView.setImageDrawable(suggestionIcon);
            }
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
