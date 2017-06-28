/*Top Secret*/
package com.dollyphin.kidszone.lockscreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dollyphin.kidszone.R;

public class ViewCheckItemAdapter extends BaseAdapter {

    private String[] mData;
    private LayoutInflater layoutInflater;
    private onItemClickListener mListener;
    public static final int DEFAULT_SELCLT = 1;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public ViewCheckItemAdapter(Context context, String[] strings) {
        mData = strings;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.length;
    }

    @Override
    public String getItem(int position) {
        return mData[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int temp = DEFAULT_SELCLT;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.view_check_item, null);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.checkboxTxt.setText(mData[position]);
        holder.checkboxItem.setChecked(false);
        holder.setPosition(position);
        if (temp == holder.getPosition()) {
            holder.checkboxItem.toggle();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (temp == holder.getPosition()) return;
                temp = holder.getPosition();
                if (mListener != null) mListener.onItemClick(temp);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public class ViewHolder {
        public CheckBox checkboxItem;
        public TextView checkboxTxt;
        private int position;

        public ViewHolder(View view) {
            checkboxItem = (CheckBox) view.findViewById(R.id.checkbox_item);
            checkboxTxt = (TextView) view.findViewById(R.id.checkbox_txt);
        }

        private void setPosition(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
}
