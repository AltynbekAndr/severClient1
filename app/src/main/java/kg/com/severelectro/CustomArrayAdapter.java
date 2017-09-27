package kg.com.severelectro;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private ArrayList list;
    private ArrayList list2;
    private ArrayList list3;
    private ArrayList list4;

    public CustomArrayAdapter(Context context,ArrayList list,ArrayList list2,ArrayList list3,ArrayList list4) {
        this.list = list;
        this.list2 = list2;
        this.list3 = list3;
        this.list4 = list4;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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
        ViewHolder holder;
        View v = convertView;
        if ( v == null){
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.custom_list, parent, false);
            holder.textView69 = (TextView) v.findViewById(R.id.textView69);
            holder.textView70 = (TextView) v.findViewById(R.id.textView70);
            holder.textView71 = (TextView) v.findViewById(R.id.textView6);
            holder.textView72 = (TextView) v.findViewById(R.id.textView7);
            holder.textView73 = (TextView) v.findViewById(R.id.textView8);
            v.setTag(holder);
        }
        holder = (ViewHolder) v.getTag();
        holder.textView69.setText(""+position);
        holder.textView70.setText(""+list.get(position));
        holder.textView71.setText(""+list2.get(position));
        holder.textView72.setText(""+list3.get(position));
        holder.textView73.setText(""+list4.get(position));

        return v;

    }
    private static class ViewHolder {
        private TextView textView69;
        private TextView textView70;
        private TextView textView71;
        private TextView textView72;
        private TextView textView73;
    }

}