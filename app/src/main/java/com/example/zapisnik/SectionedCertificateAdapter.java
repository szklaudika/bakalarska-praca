package com.example.zapisnik;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SectionedCertificateAdapter extends BaseAdapter {
    private List<ListItem> items;
    private LayoutInflater inflater;

    public SectionedCertificateAdapter(Context context, List<ListItem> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return 2; // HEADER a ITEM
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == ListItem.TYPE_HEADER) {
                convertView = inflater.inflate(R.layout.list_item_header, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.list_item_certificate, parent, false);
            }
        }
        ListItem item = items.get(position);
        if (viewType == ListItem.TYPE_HEADER) {
            TextView tvHeader = convertView.findViewById(R.id.tvHeader);
            tvHeader.setText(item.getText());
        } else {
            TextView tvItem = convertView.findViewById(R.id.tvItem);
            tvItem.setText(item.getText());
        }
        return convertView;
    }
}
