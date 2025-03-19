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

        if (viewType == ListItem.TYPE_HEADER) {
            HeaderViewHolder headerHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_header, parent, false);
                headerHolder = new HeaderViewHolder();
                headerHolder.tvHeader = convertView.findViewById(R.id.tvHeader);
                convertView.setTag(headerHolder);
            } else {
                headerHolder = (HeaderViewHolder) convertView.getTag();
            }
            headerHolder.tvHeader.setText(items.get(position).getText());
        } else { // TYPE_ITEM
            CertificateViewHolder certHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_certificate, parent, false);
                certHolder = new CertificateViewHolder();
                certHolder.tvExpiryDate = convertView.findViewById(R.id.tv_expiry_date);
                certHolder.tvCertificateDetails = convertView.findViewById(R.id.tv_certificate_details);
                convertView.setTag(certHolder);
            } else {
                certHolder = (CertificateViewHolder) convertView.getTag();
            }
            ListItem item = items.get(position);
            certHolder.tvExpiryDate.setText(item.getDate());
            certHolder.tvCertificateDetails.setText(item.getText());
        }
        return convertView;
    }

    // ViewHolder pre hlavičky
    private static class HeaderViewHolder {
        TextView tvHeader;
    }

    // ViewHolder pre certifikáty
    private static class CertificateViewHolder {
        TextView tvExpiryDate;
        TextView tvCertificateDetails;
    }
}
