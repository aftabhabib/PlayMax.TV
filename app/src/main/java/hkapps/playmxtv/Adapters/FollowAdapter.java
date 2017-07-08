package hkapps.playmxtv.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hkapps.playmxtv.Model.Enlace;
import hkapps.playmxtv.R;

/**
 * Created by hkfuertes on 20/05/2017.
 */

public class FollowAdapter extends BaseAdapter {

    String selected;
    String[] options;
    String[] labels;

    public FollowAdapter(String[] labels, String[] options, String selected){
        this.labels = labels;
        this.options = options;
        this.selected = selected;
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public String getItem(int position) {
        return labels[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(new ViewHolder(convertView));

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.name.setText(getItem(position));

        //Log.d("follow", options[position]+" "+selected);
        if(options[position].equalsIgnoreCase(selected)){
            holder.selected.setVisibility(View.VISIBLE);
        }else{
            holder.selected.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private class ViewHolder{
        public TextView name;
        public ImageView selected;

        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.nombre);
            selected = (ImageView) view.findViewById(R.id.selected);
        }
    }
}
